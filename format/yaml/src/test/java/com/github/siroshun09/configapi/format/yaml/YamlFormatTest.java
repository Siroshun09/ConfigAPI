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

package com.github.siroshun09.configapi.format.yaml;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatValue;
import com.github.siroshun09.configapi.core.node.IntValue;
import com.github.siroshun09.configapi.core.node.LongValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.format.yaml.comment.YamlBlockComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlInlineComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlNodeComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlRootComment;
import com.github.siroshun09.configapi.test.shared.file.TextFileFormatTest;
import com.github.siroshun09.configapi.test.shared.util.Replacer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.siroshun09.configapi.test.shared.util.NodeFactory.mapNode;

class YamlFormatTest extends TextFileFormatTest<MapNode, YamlFormat> {

    @Override
    protected Stream<YamlFormat> fileFormats() {
        return Stream.of(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING);
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

    private static final String VALUE_TEST_YAML =
            """
                    boolean: true
                    byte: 1
                    char: a
                    double: 3.14
                    enum: B
                    float: 3.14
                    int: 1
                    long: 1
                    short: 1
                    string: test
                    """;

    private static final String LIST_TEST_YAML =
            """
                    list:
                    - a
                    - true
                    - 1
                    - 3.14
                    - - a
                      - b
                      - c
                    - key: value
                    """;

    private static final String MAP_TEST_YAML =
            """
                    map:
                      key: value
                      nested:
                        key: value
                    """;

    private static final String ARRAY_TEST_YAML =
            """
                    boolean-array: [true, false, true]
                    char-array: [a, b, c]
                    double-array: [-1.5, 0.0, 1.5]
                    float-array: [-1.5, 0.0, 1.5]
                    int-array: [-1, 0, 1]
                    long-array: [-1, 0, 1]
                    short-array: [-1, 0, 1]
                    """;

    private static final String COMMENT_TEST_YAML =
            """
                    # header
                                
                    # key
                    # block
                    # comment
                    test: true # inline
                    # footer
                    """;

    @Override
    protected Stream<TestCase<MapNode, YamlFormat>> testCases() {
        return Stream.of(
                testCase(
                        VALUE_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.set("boolean", BooleanValue.TRUE);
                            mapNode.set("byte", new ByteValue((byte) 1));
                            mapNode.set("char", new CharValue('a'));
                            mapNode.set("double", new DoubleValue(3.14));
                            mapNode.set("enum", new EnumValue<>(SharedEnum.B));
                            mapNode.set("float", new FloatValue(3.14f));
                            mapNode.set("int", new IntValue(1));
                            mapNode.set("long", new LongValue(1L));
                            mapNode.set("short", new ShortValue((short) 1));
                            mapNode.set("string", StringValue.fromString("test"));
                        })
                ).saveTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        VALUE_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.set("boolean", BooleanValue.TRUE);
                            mapNode.set("byte", new ByteValue((byte) 1));
                            mapNode.set("char", new StringValue("a")); // char will be string
                            mapNode.set("double", new DoubleValue(3.14));
                            mapNode.set("enum", StringValue.fromString("B")); // enum will be saved as Enum#name
                            mapNode.set("float", new FloatValue(3.14f));
                            mapNode.set("int", new IntValue(1));
                            mapNode.set("long", new LongValue(1L));
                            mapNode.set("short", new ShortValue((short) 1));
                            mapNode.set("string", StringValue.fromString("test"));
                        })
                ).loadTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        LIST_TEST_YAML,
                        mapNode(mapNode -> mapNode.createList("list").addAll(List.of(
                                "a",
                                true,
                                1,
                                3.14,
                                List.of("a", "b", "c"),
                                Map.of("key", "value")
                        )))
                ).saveAndLoadTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        MAP_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.createMap("map").set("key", "value");
                            mapNode.getOrCreateMap("map").set("nested", mapNode(nested -> nested.set("key", "value")));
                        })
                ).saveAndLoadTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        ARRAY_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.set("boolean-array", new boolean[]{true, false, true});
                            // byte array is handled as binary in Yaml
                            mapNode.set("char-array", new char[]{'a', 'b', 'c'});
                            mapNode.set("double-array", new double[]{-1.5, 0.0, 1.5});
                            mapNode.set("float-array", new float[]{-1.5f, 0.0f, 1.5f});
                            mapNode.set("int-array", new int[]{-1, 0, 1});
                            mapNode.set("long-array", new long[]{-1, 0, 1});
                            mapNode.set("short-array", new short[]{-1, 0, 1});
                        })
                ).saveTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        ARRAY_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.createList("boolean-array").addAll(List.of(true, false, true));
                            // byte array is handled as binary in Yaml
                            mapNode.createList("char-array").addAll(List.of("a", "b", "c")); // char array will be string list
                            mapNode.createList("double-array").addAll(List.of(-1.5, 0.0, 1.5));
                            mapNode.createList("float-array").addAll(List.of(-1.5f, 0.0f, 1.5f));
                            mapNode.createList("int-array").addAll(List.of(-1, 0, 1));
                            mapNode.createList("long-array").addAll(List.of(-1, 0, 1));
                            mapNode.createList("short-array").addAll(List.of(-1, 0, 1));
                        })
                ).loadTest(YamlFormat.DEFAULT, YamlFormat.COMMENT_PROCESSING),
                testCase(
                        COMMENT_TEST_YAML,
                        mapNode(mapNode -> {
                            mapNode.setComment(new YamlRootComment(new YamlBlockComment(" header", 0), new YamlBlockComment(" footer", 0)));
                            mapNode.set("test", CommentableNode.withComment(BooleanValue.TRUE, new YamlNodeComment(new YamlBlockComment(Replacer.lines(" key\n block\n comment"), 0), new YamlInlineComment(" inline"))));
                        })
                ).saveAndLoadTest(YamlFormat.COMMENT_PROCESSING),
                testCase(
                        COMMENT_TEST_YAML,
                        mapNode(mapNode -> mapNode.set("test", BooleanValue.TRUE))
                ).loadTest(YamlFormat.DEFAULT),
                testCase(
                        """
                                key: value # test
                                """,
                        mapNode(mapNode -> mapNode.set("key", CommentableNode.withComment(new StringValue("value"), SimpleComment.create("test", "inline"))))
                ).saveTest(YamlFormat.COMMENT_PROCESSING),
                testCase(
                        """
                                map:
                                    key: value
                                    nested:
                                        key: value
                                """,
                        mapNode(mapNode -> {
                            mapNode.createMap("map").set("key", "value");
                            mapNode.getOrCreateMap("map").createMap("nested").set("key", "value");
                        })
                ).saveAndLoadTest(new YamlFormat.Builder().indent(4).build()),
                testCase(
                        """
                                array:
                                - a
                                - b
                                - c
                                """,
                        mapNode(mapNode -> mapNode.set("array", new char[]{'a', 'b', 'c'}))
                ).saveTest(new YamlFormat.Builder().arrayFlowStyle(DumperOptions.FlowStyle.BLOCK).build()),
                testCase(
                        """
                                list: [a, b, c]
                                """,
                        mapNode(mapNode -> mapNode.createList("list").addAll(List.of("a", "b", "c")))
                ).saveAndLoadTest(new YamlFormat.Builder().sequenceFlowStyle(DumperOptions.FlowStyle.FLOW).build()),
                testCase(
                        """
                                map-1: {key: value}
                                map-2: {nested: {key: value}}
                                """,
                        mapNode(mapNode -> {
                            mapNode.createMap("map-1").set("key", "value");
                            mapNode.getOrCreateMap("map-2").createMap("nested").set("key", "value");
                        })
                ).saveAndLoadTest(new YamlFormat.Builder().mapFlowStyle(DumperOptions.FlowStyle.FLOW).build()),
                testCase(
                        """
                                {string: test, list: [a, b, c], map: {key: value}}
                                """,
                        mapNode(mapNode -> {
                            mapNode.set("string", "test");
                            mapNode.set("list", List.of("a", "b", "c"));
                            mapNode.set("map", Map.of("key", "value"));
                        })
                ).saveAndLoadTest(new YamlFormat.Builder().flowStyle(DumperOptions.FlowStyle.FLOW).build()),
                testCase(
                        """
                                "string": "3.14"
                                """,
                        mapNode(mapNode -> mapNode.set("string", "3.14"))
                ).saveAndLoadTest(new YamlFormat.Builder().scalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED).build()),
                testCase(
                        """
                                'string': '3.14'
                                """,
                        mapNode(mapNode -> mapNode.set("string", "3.14"))
                ).saveAndLoadTest(new YamlFormat.Builder().scalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED).build())
        ).flatMap(Function.identity());
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
