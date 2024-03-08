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

package com.github.siroshun09.configapi.test.shared.file;

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import com.github.siroshun09.configapi.test.shared.util.Replacer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An abstract class for testing {@link FileFormat} implementation.
 *
 * @param <N> a {@link Node} type
 * @param <F> a {@link FileFormat} type
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BasicFileFormatTest<N extends Node<?>, F extends FileFormat<N>> {

    /**
     * Creates a sample {@link MapNode}.
     *
     * @return a sample {@link MapNode}
     */
    public static @NotNull MapNode createSharedMapNode() {
        var mapNode = MapNode.create();

        mapNode.set("string", "value");
        mapNode.set("integer", 100);
        mapNode.set("double", 3.14);
        mapNode.set("bool", true);
        mapNode.set("list", List.of("A", "B", "C"));
        mapNode.set("map", Map.of("key", "value"));
        mapNode.set("nested", Map.of("map", Map.of("key", "value")));

        return mapNode;
    }

    /**
     * An enum that can be used for testing.
     */
    public enum SharedEnum {
        /**
         * Sample enum value: A
         */
        A,
        /**
         * Sample enum value: B
         */
        B,
        /**
         * Sample enum value: C
         */
        C
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadFromFilepath(@NotNull Sample<N, F> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("load-from-filepath" + this.extension());
        Files.createFile(filepath);
        Files.writeString(filepath, sample.text(), StandardCharsets.UTF_8);
        this.checkFileLoading(sample, filepath);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadFromInputStream(@NotNull Sample<N, F> sample) throws IOException {
        try (var input = new ByteArrayInputStream(sample.text().getBytes(StandardCharsets.UTF_8))) {
            NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(input));
        }
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadFromReader(@NotNull Sample<N, F> sample) throws IOException {
        try (var reader = new StringReader(sample.text())) {
            NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(reader));
        }
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveToFilepath(@NotNull Sample<N, F> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("save-to-filepath" + this.extension());
        Files.createFile(filepath);

        sample.fileFormat().save(sample.node(), filepath);
        this.checkText(sample.text(), Files.readString(filepath));
        this.checkFileLoading(sample, filepath);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveToOutputStream(@NotNull Sample<N, F> sample) throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            sample.fileFormat().save(sample.node(), output);
            this.checkText(sample.text(), output.toString(StandardCharsets.UTF_8));

            try (var input = new ByteArrayInputStream(output.toByteArray())) {
                NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(input));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveToWriter(@NotNull Sample<N, F> sample) throws IOException {
        try (var writer = new StringWriter()) {
            sample.fileFormat().save(sample.node(), writer);
            this.checkText(sample.text(), writer.toString());

            try (var reader = new StringReader(writer.toString())) {
                NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(reader));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testNonExistentFile(@NotNull Sample<N, F> sample, @TempDir Path directory) throws IOException {
        var filename = "non-existent-file" + this.extension();
        NodeAssertion.assertEquals(this.emptyNode(), sample.fileFormat().load(directory.resolve(filename)));
        NodeAssertion.assertEquals(this.emptyNode(), sample.fileFormat().load(directory.resolve("non-existent-directory").resolve(filename)));
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveInNonExistentDirectory(@NotNull Sample<N, F> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("new-directory").resolve("new-file" + this.extension());
        sample.fileFormat().save(sample.node(), filepath);
        this.checkText(sample.text(), Files.readString(filepath));
        this.checkFileLoading(sample, filepath);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testEmptyFile(@NotNull Sample<N, F> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("empty" + this.extension());
        Files.createFile(filepath);

        if (this.supportEmptyFile()) {
            this.checkFileLoading(new Sample<>(sample.fileFormat(), this.emptyNode(), ""), filepath);
        } else {
            Assertions.assertThrows(IOException.class, () -> sample.fileFormat().load(filepath));
            Assertions.assertThrows(IOException.class, () -> {
                try (var in = Files.newInputStream(filepath)) {
                    sample.fileFormat().load(in);
                }
            });
            Assertions.assertThrows(IOException.class, () -> {
                try (var reader = Files.newBufferedReader(filepath)) {
                    sample.fileFormat().load(reader);
                }
            });
        }
    }

    /**
     * Gets the sample {@link Node}s that are used for testing.
     *
     * @return the sample {@link Node}s that are used for testing
     */
    protected abstract @NotNull Stream<Sample<N, F>> samples();

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
     * Checks the file content.
     *
     * @param sample the expected {@link Node} as a result of loading file
     * @param filepath the filepath
     * @throws IOException if I/O error occurred
     */
    protected void checkFileLoading(@NotNull Sample<N, F> sample, @NotNull Path filepath) throws IOException {
        NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(filepath));

        try (var in = Files.newInputStream(filepath)) {
            NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(in));
        }

        try (var reader = Files.newBufferedReader(filepath)) {
            NodeAssertion.assertEquals(sample.node(), sample.fileFormat().load(reader));
        }
    }

    private void checkText(@NotNull String expected, @NotNull String actual) {
        Assertions.assertEquals(Replacer.lines(expected), Replacer.lines(actual));
    }

    /**
     * A record to define sample data.
     *
     * @param fileFormat the {@link FileFormat}
     * @param node the sample {@link Node}
     * @param text the expected output text
     * @param <N> the {@link Node} type
     * @param <F> the {@link FileFormat} type
     */
    public record Sample<N extends Node<?>, F extends FileFormat<N>>(@NotNull F fileFormat, @NotNull N node,
                                                                     @NotNull String text) {
    }
}
