/*
 *     Copyright 2023 Siroshun09
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

package com.github.siroshun09.configapi.core.file.java.binary;

import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class BinaryFormatTest {

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadingAndSavingFile(@NotNull Node<?> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("loading-and-saving-filepath.dat");
        Files.createFile(filepath);

        BinaryFormat.DEFAULT.save(sample, filepath);
        this.checkFileLoading(sample, filepath);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadingAndSavingStream(@NotNull Node<?> sample) throws IOException {
        byte[] bytes;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryFormat.DEFAULT.save(sample, out);
            bytes = out.toByteArray();
        }

        Node<?> loaded;

        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            loaded = BinaryFormat.DEFAULT.load(in);
        }

        NodeAssertion.assertEquals(sample, loaded);
    }

    @Test
    void testNonExistentFile(@TempDir Path directory) throws IOException {
        var filename = "non-existent-file.dat";
        NodeAssertion.assertEquals(NullNode.NULL, BinaryFormat.DEFAULT.load(directory.resolve(filename)));
        NodeAssertion.assertEquals(NullNode.NULL, BinaryFormat.DEFAULT.load(directory.resolve("non-existent-directory").resolve(filename)));
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveInNonExistentDirectory(@NotNull Node<?> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("new-directory").resolve("new-file.dat");
        BinaryFormat.DEFAULT.save(sample, filepath);
        this.checkFileLoading(sample, filepath);
    }

    private static @NotNull Stream<Node<?>> samples() {
        return Stream.of(
                Samples.mapNode()
        );
    }

    private void checkFileLoading(@NotNull Node<?> sample, @NotNull Path filepath) throws IOException {
        NodeAssertion.assertEquals(sample, BinaryFormat.DEFAULT.load(filepath));

        try (var in = Files.newInputStream(filepath)) {
            NodeAssertion.assertEquals(sample, BinaryFormat.DEFAULT.load(in));
        }
    }
}
