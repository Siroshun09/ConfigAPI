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

package com.github.siroshun09.configapi.format.yaml;

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class YamlFormatTest {

    private static final String YAML_EXAMPLE = """
            string: value
            integer: 100
            double: 3.14
            bool: true
            list:
            - A
            - B
            - C
            map:
              key: value
            nested:
              map:
                key: value
            """;

    @Test
    void testLoadFromFilepath(@TempDir Path directory) throws IOException {
        var path = directory.resolve("load-from-filepath.yml");

        try {
            Files.createFile(path);
            Files.writeString(path, YAML_EXAMPLE, StandardCharsets.UTF_8);
            var node = YamlFormat.DEFAULT.load(path);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void testLoadFromInputStream() throws IOException {
        try (var input = new ByteArrayInputStream(YAML_EXAMPLE.getBytes(StandardCharsets.UTF_8))) {
            var node = YamlFormat.DEFAULT.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }
    }

    @Test
    void testLoadFromReader() throws IOException {
        try (var input = new StringReader(YAML_EXAMPLE)) {
            var node = YamlFormat.DEFAULT.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }
    }

    @Test
    void testEmptyFile() throws IOException {
        var path = Path.of("empty.yml");

        try {
            Files.createFile(path);
            var node = YamlFormat.DEFAULT.load(path);
            Assertions.assertNotNull(node);
            Assertions.assertTrue(node.value().isEmpty());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void testSaveToFilepath(@TempDir Path directory) throws IOException {
        var path = directory.resolve("save-to-filepath.yml");

        try {
            YamlFormat.DEFAULT.save(Samples.mapNode(), path);
            Assertions.assertEquals(YAML_EXAMPLE, Files.readString(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void testSaveToOutputStream() throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            YamlFormat.DEFAULT.save(Samples.mapNode(), output);
            Assertions.assertEquals(YAML_EXAMPLE, output.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    void testSaveToWriter() throws IOException {
        try (var writer = new StringWriter()) {
            YamlFormat.DEFAULT.save(Samples.mapNode(), writer);
            Assertions.assertEquals(YAML_EXAMPLE, writer.toString());
        }
    }

    @Test
    void testEnumValue() throws IOException {
        MapNode loaded;

        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("enum", Samples.Enum.A);
            YamlFormat.DEFAULT.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                loaded = YamlFormat.DEFAULT.load(reader);
            }
        }

        Assertions.assertEquals(StringValue.fromString("A"), loaded.get("enum"));
    }

    @Test
    void testNotSupportedObject() throws IOException {
        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("custom", new CustomObject());
            Assertions.assertThrows(IOException.class, () -> YamlFormat.DEFAULT.save(mapNode, writer));
        }

        try (var reader = new StringReader(CustomObject.YAML)) {
            Assertions.assertThrows(IOException.class, () -> YamlFormat.DEFAULT.load(reader));
        }
    }

    @Test
    void testNonExistentFile(@TempDir Path directory) throws IOException {
        Path filepath = directory.resolve("test.yml");
        NodeAssertion.assertEquals(MapNode.empty(), YamlFormat.DEFAULT.load(filepath));
    }

    @Test
    void testSaveInNonExistentDirectory(@TempDir Path directory) throws IOException {
        Path filepath = directory.resolve("parent").resolve("test.json");
        MapNode expected = Samples.mapNode();
        YamlFormat.DEFAULT.save(expected, filepath);
        NodeAssertion.assertEquals(expected, YamlFormat.DEFAULT.load(filepath));
    }

    private static class CustomObject {
        private static final String YAML = "custom: !!com.github.siroshun09.configapi.format.yaml.test.YamlFormatTest$CustomObject {}";
        private final int value = 100;
    }
}
