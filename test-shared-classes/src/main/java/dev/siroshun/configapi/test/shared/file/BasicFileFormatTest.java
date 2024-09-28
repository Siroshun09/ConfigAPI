/*
 *     Copyright 2024 Siroshun09
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package dev.siroshun.configapi.test.shared.file;

import dev.siroshun.configapi.core.file.FileFormat;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * An abstract class for testing {@link FileFormat} implementation.
 *
 * @param <N> a {@link Node} type
 * @param <F> a {@link FileFormat} type
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BasicFileFormatTest<N extends Node<?>, F extends FileFormat<N>> {

    @ParameterizedTest
    @MethodSource("fileFormats")
    void testNonExistentFile(F fileFormat, @TempDir Path directory) throws IOException {
        var filename = "non-existent-file" + this.extension();

        if (this.isSupportedIOType(Path.class)) {
            NodeAssertion.assertEquals(this.emptyNode(), fileFormat.load(directory.resolve(filename)));
            NodeAssertion.assertEquals(this.emptyNode(), fileFormat.load(directory.resolve("non-existent-directory").resolve(filename)));
        }
    }

    @ParameterizedTest
    @MethodSource("fileFormats")
    void testSaveInNonExistentDirectory(F fileFormat, @TempDir Path directory) throws IOException {
        if (!this.isSupportedIOType(Path.class)) {
            return;
        }

        var newDirectory = directory.resolve("new-directory");
        var filepath = newDirectory.resolve("new-file" + this.extension());

        fileFormat.save(this.emptyNode(), filepath);

        Assertions.assertTrue(Files.isDirectory(newDirectory));
        Assertions.assertTrue(Files.isRegularFile(filepath));

        this.checkLoadingEmptyNodeFromFile(fileFormat, filepath);
    }

    @ParameterizedTest
    @MethodSource("fileFormats")
    void testEmptyFileLoading(F fileFormat, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("empty" + this.extension());
        Files.createFile(filepath);

        if (this.supportEmptyFile()) {
            this.checkLoadingEmptyNodeFromFile(fileFormat, filepath);
        } else {
            if (this.isSupportedIOType(Path.class)) {
                Assertions.assertThrows(IOException.class, () -> fileFormat.load(filepath));
            }

            if (this.isSupportedIOType(InputStream.class)) {
                Assertions.assertThrows(IOException.class, () -> {
                    try (var in = Files.newInputStream(filepath)) {
                        fileFormat.load(in);
                    }
                });
            }

            if (this.isSupportedIOType(Reader.class)) {
                Assertions.assertThrows(IOException.class, () -> {
                    try (var reader = Files.newBufferedReader(filepath)) {
                        fileFormat.load(reader);
                    }
                });
            }
        }
    }

    @ParameterizedTest
    @MethodSource("fileFormats")
    void testUnsupportedIOTypes(F fileFormat, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("unsupported" + this.extension());
        Files.createFile(filepath);

        if (!this.isSupportedIOType(Path.class)) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> fileFormat.save(this.emptyNode(), filepath));
            Assertions.assertThrows(UnsupportedOperationException.class, () -> fileFormat.load(filepath));
        }

        if (!this.isSupportedIOType(OutputStream.class)) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (var output = Files.newOutputStream(filepath)) {
                    fileFormat.save(this.emptyNode(), output);
                }
            });
        }

        if (!this.isSupportedIOType(InputStream.class)) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (var input = Files.newInputStream(filepath)) {
                    fileFormat.load(input);
                }
            });
        }

        if (!this.isSupportedIOType(Writer.class)) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (var writer = Files.newBufferedWriter(filepath)) {
                    fileFormat.save(this.emptyNode(), writer);
                }
            });
        }

        if (!this.isSupportedIOType(Reader.class)) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (var reader = Files.newBufferedReader(filepath)) {
                    fileFormat.load(reader);
                }
            });
        }
    }

    private void checkLoadingEmptyNodeFromFile(F fileFormat, Path filepath) throws IOException {
        if (this.isSupportedIOType(Path.class)) {
            try {
                NodeAssertion.assertEquals(this.emptyNode(), fileFormat.load(filepath));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (this.isSupportedIOType(InputStream.class)) {
            try (var in = Files.newInputStream(filepath)) {
                NodeAssertion.assertEquals(this.emptyNode(), fileFormat.load(in));
            }
        }

        if (this.isSupportedIOType(Reader.class)) {
            try (var reader = Files.newBufferedReader(filepath)) {
                NodeAssertion.assertEquals(this.emptyNode(), fileFormat.load(reader));
            }
        }
    }

    /**
     * Gets the {@link FileFormat}s to test.
     *
     * @return the {@link FileFormat}s
     */
    protected abstract Stream<F> fileFormats();

    /**
     * Gets the extension of files.
     *
     * @return the extension of files
     */
    protected abstract @NotNull String extension();

    /**
     * Gets the empty (default) {@link Node} that is returned when the file does not exist.
     *
     * @return the empty (default) {@link Node}
     */
    protected abstract @NotNull N emptyNode();

    /**
     * Checks if the {@link FileFormat} supports loading from an empty file.
     *
     * @return {@code true} if the {@link FileFormat} can load from an empty file, otherwise {@code false}
     */
    protected abstract boolean supportEmptyFile();

    /**
     * Checks if the given in/out type is supported.
     * <p>
     * Current io types: {@link Path}, {@link InputStream}, {@link OutputStream}, {@link Reader}, and {@link Writer}
     *
     * @param ioType a {@link Class} to check
     * @return {@code true} if the {@link FileFormat} supports the given in/out type, otherwise {@code false}
     */
    protected boolean isSupportedIOType(Class<?> ioType) {
        return true;
    }

    /**
     * A shared enum to use for testing {@link EnumValue}.
     */
    protected enum SharedEnum {
        /**
         * A
         */
        A,
        /**
         * B
         */
        B,
        /**
         * C
         */
        C
    }
}
