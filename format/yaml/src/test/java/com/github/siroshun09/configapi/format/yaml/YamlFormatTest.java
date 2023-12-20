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
import com.github.siroshun09.configapi.test.shared.file.BasicFileFormatTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

class YamlFormatTest extends BasicFileFormatTest<MapNode, YamlFormat> {

    private static final String SAMPLE_MAP_NODE_YAML = """
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

    @Override
    protected @NotNull Stream<Sample<MapNode, YamlFormat>> samples() {
        return Stream.of(
                new Sample<>(YamlFormat.DEFAULT, Samples.mapNode(), SAMPLE_MAP_NODE_YAML),
                new Sample<>(YamlFormat.COMMENT_PROCESSING, Samples.mapNode(), SAMPLE_MAP_NODE_YAML)
        );
    }

    @Override
    protected @NotNull String extension() {
        return ".yml";
    }

    @Override
    protected @NotNull MapNode emptyNode() {
        return MapNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return true;
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

    private static class CustomObject {
        private static final String YAML = "custom: !!com.github.siroshun09.configapi.format.yaml.test.YamlFormatTest$CustomObject {}";
        private final int value = 100;
    }
}
