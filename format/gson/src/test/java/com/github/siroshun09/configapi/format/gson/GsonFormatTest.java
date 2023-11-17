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

package com.github.siroshun09.configapi.format.gson;

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.format.gson.GsonFormat;
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

class GsonFormatTest {

    private static final String JSON_EXAMPLE = "{\"string\":\"value\",\"integer\":100,\"double\":3.14,\"bool\":true,\"list\":[\"A\",\"B\",\"C\"],\"map\":{\"key\":\"value\"},\"nested\":{\"map\":{\"key\":\"value\"}}}";
    private static final String PRETTY_PRINTING_EXAMPLE = """
            {
              "string": "value",
              "integer": 100,
              "double": 3.14,
              "bool": true,
              "list": [
                "A",
                "B",
                "C"
              ],
              "map": {
                "key": "value"
              },
              "nested": {
                "map": {
                  "key": "value"
                }
              }
            }""";

    @Test
    void testLoadFromFilepath(@TempDir Path directory) throws IOException {
        var path = directory.resolve("load-from-filepath.json");

        Files.writeString(path, JSON_EXAMPLE, StandardCharsets.UTF_8);
        NodeAssertion.assertEquals(Samples.mapNode(), GsonFormat.DEFAULT.load(path));

        Files.writeString(path, PRETTY_PRINTING_EXAMPLE, StandardCharsets.UTF_8);
        NodeAssertion.assertEquals(Samples.mapNode(), GsonFormat.PRETTY_PRINTING.load(path));
    }

    @Test
    void testLoadFromInputStream() throws IOException {
        try (var input = new ByteArrayInputStream(JSON_EXAMPLE.getBytes(StandardCharsets.UTF_8))) {
            var node = GsonFormat.DEFAULT.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }

        try (var input = new ByteArrayInputStream(PRETTY_PRINTING_EXAMPLE.getBytes(StandardCharsets.UTF_8))) {
            var node = GsonFormat.PRETTY_PRINTING.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }
    }

    @Test
    void testLoadFromReader() throws IOException {
        try (var input = new StringReader(JSON_EXAMPLE)) {
            var node = GsonFormat.DEFAULT.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }

        try (var input = new StringReader(PRETTY_PRINTING_EXAMPLE)) {
            var node = GsonFormat.PRETTY_PRINTING.load(input);
            NodeAssertion.assertEquals(Samples.mapNode(), node);
        }
    }

    @Test
    void testEmptyFile(@TempDir Path dir) throws IOException {
        var path = dir.resolve("empty.yml");

        Files.createFile(path);

        var node = GsonFormat.DEFAULT.load(path);
        Assertions.assertNotNull(node);
        Assertions.assertTrue(node.value().isEmpty());
    }

    @Test
    void testSaveToFilepath(@TempDir Path directory) throws IOException {
        var path = directory.resolve("save-to-filepath.yml");

        GsonFormat.DEFAULT.save(Samples.mapNode(), path);
        Assertions.assertEquals(JSON_EXAMPLE, Files.readString(path));

        GsonFormat.PRETTY_PRINTING.save(Samples.mapNode(), path);
        Assertions.assertEquals(PRETTY_PRINTING_EXAMPLE, Files.readString(path));
    }

    @Test
    void testSaveToOutputStream() throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            GsonFormat.DEFAULT.save(Samples.mapNode(), output);
            Assertions.assertEquals(JSON_EXAMPLE, output.toString(StandardCharsets.UTF_8));
        }

        try (var output = new ByteArrayOutputStream()) {
            GsonFormat.PRETTY_PRINTING.save(Samples.mapNode(), output);
            Assertions.assertEquals(PRETTY_PRINTING_EXAMPLE, output.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    void testSaveToWriter() throws IOException {
        try (var writer = new StringWriter()) {
            GsonFormat.DEFAULT.save(Samples.mapNode(), writer);
            Assertions.assertEquals(JSON_EXAMPLE, writer.toString());
        }

        try (var writer = new StringWriter()) {
            GsonFormat.PRETTY_PRINTING.save(Samples.mapNode(), writer);
            Assertions.assertEquals(PRETTY_PRINTING_EXAMPLE, writer.toString());
        }
    }

    @Test
    void testEnumValue() throws IOException {
        MapNode loaded;

        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("enum", Samples.Enum.A);
            GsonFormat.DEFAULT.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                loaded = GsonFormat.DEFAULT.load(reader);
            }
        }

        Assertions.assertEquals(StringValue.fromString("A"), loaded.get("enum"));
    }

    @Test
    void testDefaults() throws IOException {
        var mapNode = Samples.mapNode();

        try (var writer = new StringWriter()) {
            GsonFormat.DEFAULT.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                NodeAssertion.assertEquals(mapNode, GsonFormat.PRETTY_PRINTING.load(reader));
            }
        }

        try (var writer = new StringWriter()) {
            GsonFormat.PRETTY_PRINTING.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                NodeAssertion.assertEquals(mapNode, GsonFormat.DEFAULT.load(reader));
            }
        }
    }

    @Test
    void testNotSupportedObject() throws IOException {
        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("custom", new CustomObject());
            Assertions.assertThrows(IOException.class, () -> GsonFormat.DEFAULT.save(mapNode, writer));
        }
    }

    private static class CustomObject {
        private final int value = 100;
    }
}
