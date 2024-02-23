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

package com.github.siroshun09.configapi.format.jackson;

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.file.BasicFileFormatTest;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

class JacksonFormatTest extends BasicFileFormatTest<MapNode, JacksonFormat> {

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

    @Override
    protected @NotNull Stream<Sample<MapNode, JacksonFormat>> samples() {
        return Stream.of(
                new Sample<>(JacksonFormat.DEFAULT, Samples.mapNode(), JSON_EXAMPLE),
                new Sample<>(JacksonFormat.PRETTY_PRINTING, Samples.mapNode(), PRETTY_PRINTING_EXAMPLE)
        );
    }

    @Override
    protected @NotNull String extension() {
        return ".json";
    }

    @Override
    protected @NotNull MapNode emptyNode() {
        return MapNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return false;
    }

    @Test
    void testEnumValue() throws IOException {
        MapNode loaded;

        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("enum", Samples.Enum.A);
            JacksonFormat.DEFAULT.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                loaded = JacksonFormat.DEFAULT.load(reader);
            }
        }

        Assertions.assertEquals(StringValue.fromString("A"), loaded.get("enum"));
    }

    @Test
    void testDefaultAndPrettyPrinting() throws IOException {
        var mapNode = Samples.mapNode();

        try (var writer = new StringWriter()) {
            JacksonFormat.DEFAULT.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                NodeAssertion.assertEquals(mapNode, JacksonFormat.PRETTY_PRINTING.load(reader));
            }
        }

        try (var writer = new StringWriter()) {
            JacksonFormat.PRETTY_PRINTING.save(mapNode, writer);

            try (var reader = new StringReader(writer.toString())) {
                NodeAssertion.assertEquals(mapNode, JacksonFormat.DEFAULT.load(reader));
            }
        }
    }

    @Test
    void testNotSupportedObject() throws IOException {
        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("custom", new CustomObject());
            Assertions.assertThrows(IOException.class, () -> JacksonFormat.DEFAULT.save(mapNode, writer));
        }
    }

    private static class CustomObject {
        private final int value = 100;
    }
}
