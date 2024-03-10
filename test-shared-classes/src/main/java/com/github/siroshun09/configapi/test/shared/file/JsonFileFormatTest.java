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
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatValue;
import com.github.siroshun09.configapi.core.node.IntValue;
import com.github.siroshun09.configapi.core.node.LongValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.siroshun09.configapi.test.shared.util.NodeFactory.mapNode;

/**
 * An abstract class for testing {@link FileFormat} implementation of JSON.
 *
 * @param <F> a {@link FileFormat} type
 */
public abstract class JsonFileFormatTest<F extends FileFormat<MapNode>> extends TextFileFormatTest<MapNode, F> {

    private static final String VALUE_TEST_DEFAULT_JSON = "{\"boolean\":true,\"byte\":1,\"char\":\"a\",\"double\":3.14,\"enum\":\"B\",\"float\":3.14,\"int\":1,\"long\":1,\"short\":1,\"string\":\"test\"}";
    private static final String VALUE_TEST_PRETTY_PRINTING_JSON =
            """
                    {
                      "boolean": true,
                      "byte": 1,
                      "char": "a",
                      "double": 3.14,
                      "enum": "B",
                      "float": 3.14,
                      "int": 1,
                      "long": 1,
                      "short": 1,
                      "string": "test"
                    }""";

    private static MapNode valueTestMapNode() {
        return mapNode(mapNode -> {
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
        });
    }

    private static MapNode expectedValueTestMapNode() {
        var mapNode = valueTestMapNode();
        mapNode.set("char", StringValue.fromString("a")); // char will be string
        mapNode.set("enum", StringValue.fromString("B")); // enum will be saved as Enum#name
        return mapNode;
    }

    private static final String ARRAY_TEST_DEFAULT_JSON = "{\"boolean-array\":[true,false,true],\"byte-array\":[-1,0,1],\"char-array\":[\"a\",\"b\",\"c\"],\"double-array\":[-1.5,0.0,1.5],\"float-array\":[-1.5,0.0,1.5],\"int-array\":[-1,0,1],\"long-array\":[-1,0,1],\"short-array\":[-1,0,1]}";
    private static final String ARRAY_TEST_PRETTY_PRINTING_JSON =
            """
                    {
                      "boolean-array": [
                        true,
                        false,
                        true
                      ],
                      "byte-array": [
                        -1,
                        0,
                        1
                      ],
                      "char-array": [
                        "a",
                        "b",
                        "c"
                      ],
                      "double-array": [
                        -1.5,
                        0.0,
                        1.5
                      ],
                      "float-array": [
                        -1.5,
                        0.0,
                        1.5
                      ],
                      "int-array": [
                        -1,
                        0,
                        1
                      ],
                      "long-array": [
                        -1,
                        0,
                        1
                      ],
                      "short-array": [
                        -1,
                        0,
                        1
                      ]
                    }""";

    private static MapNode arrayTestMapNode() {
        return mapNode(mapNode -> {
            mapNode.set("boolean-array", new boolean[]{true, false, true});
            mapNode.set("byte-array", new byte[]{-1, 0, 1});
            mapNode.set("char-array", new char[]{'a', 'b', 'c'});
            mapNode.set("double-array", new double[]{-1.5, 0.0, 1.5});
            mapNode.set("float-array", new float[]{-1.5f, 0.0f, 1.5f});
            mapNode.set("int-array", new int[]{-1, 0, 1});
            mapNode.set("long-array", new long[]{-1, 0, 1});
            mapNode.set("short-array", new short[]{-1, 0, 1});
        });
    }

    private static MapNode expectedArrayTestMapNode() {
        return mapNode(mapNode -> {
            mapNode.createList("boolean-array").addAll(List.of(true, false, true));
            mapNode.createList("byte-array").addAll(List.of(-1, 0, 1));
            mapNode.createList("char-array").addAll(List.of("a", "b", "c")); // char array will be string list
            mapNode.createList("double-array").addAll(List.of(-1.5, 0.0, 1.5));
            mapNode.createList("float-array").addAll(List.of(-1.5f, 0.0f, 1.5f));
            mapNode.createList("int-array").addAll(List.of(-1, 0, 1));
            mapNode.createList("long-array").addAll(List.of(-1, 0, 1));
            mapNode.createList("short-array").addAll(List.of(-1, 0, 1));
        });
    }

    private static final String LIST_TEST_DEFAULT_JSON = "{\"list\":[\"a\",true,1,3.14,[\"a\",\"b\",\"c\"],{\"key\":\"value\"}]}";
    private static final String LIST_TEST_PRETTY_PRINTING_JSON =
            """
                    {
                      "list": [
                        "a",
                        true,
                        1,
                        3.14,
                        [
                          "a",
                          "b",
                          "c"
                        ],
                        {
                          "key": "value"
                        }
                      ]
                    }""";

    private static MapNode listTestMapNode() {
        return mapNode(mapNode -> mapNode.createList("list").addAll(List.of(
                "a",
                true,
                1,
                3.14,
                List.of("a", "b", "c"),
                Map.of("key", "value")
        )));
    }

    private static final String MAP_TEST_DEFAULT_JSON = "{\"map\":{\"key\":\"value\",\"nested\":{\"key\":\"value\"}}}";
    private static final String MAP_TEST_PRETTY_PRINTING_JSON =
            """
                    {
                      "map": {
                        "key": "value",
                        "nested": {
                          "key": "value"
                        }
                      }
                    }""";

    private static MapNode mapTest() {
        return mapNode(mapNode -> {
            mapNode.createMap("map").set("key", "value");
            mapNode.getOrCreateMap("map").set("nested", mapNode(nested -> nested.set("key", "value")));
        });
    }

    @Override
    protected Stream<TestCase<MapNode, F>> testCases() {
        return Stream.of(
                testCase(VALUE_TEST_DEFAULT_JSON, valueTestMapNode()).saveTest(this.defaultPrinting()),
                testCase(VALUE_TEST_PRETTY_PRINTING_JSON, valueTestMapNode()).saveTest(this.prettyPrinting()),
                testCase(VALUE_TEST_DEFAULT_JSON, expectedValueTestMapNode()).loadTest(this.defaultPrinting(), this.prettyPrinting()),
                testCase(VALUE_TEST_PRETTY_PRINTING_JSON, expectedValueTestMapNode()).loadTest(this.defaultPrinting(), this.prettyPrinting()),
                testCase(LIST_TEST_DEFAULT_JSON, listTestMapNode()).saveAndLoadTest(this.defaultPrinting()),
                testCase(LIST_TEST_PRETTY_PRINTING_JSON, listTestMapNode()).saveAndLoadTest(this.prettyPrinting()),
                testCase(LIST_TEST_DEFAULT_JSON, listTestMapNode()).loadTest(this.prettyPrinting()),
                testCase(LIST_TEST_PRETTY_PRINTING_JSON, listTestMapNode()).loadTest(this.defaultPrinting()),
                testCase(MAP_TEST_DEFAULT_JSON, mapTest()).saveAndLoadTest(this.defaultPrinting()),
                testCase(MAP_TEST_PRETTY_PRINTING_JSON, mapTest()).saveAndLoadTest(this.prettyPrinting()),
                testCase(MAP_TEST_DEFAULT_JSON, mapTest()).loadTest(this.prettyPrinting()),
                testCase(MAP_TEST_PRETTY_PRINTING_JSON, mapTest()).loadTest(this.defaultPrinting()),
                testCase(ARRAY_TEST_DEFAULT_JSON, arrayTestMapNode()).saveTest(this.defaultPrinting()),
                testCase(ARRAY_TEST_PRETTY_PRINTING_JSON, arrayTestMapNode()).saveTest(this.prettyPrinting()),
                testCase(ARRAY_TEST_DEFAULT_JSON, expectedArrayTestMapNode()).loadTest(this.defaultPrinting(), this.prettyPrinting()),
                testCase(ARRAY_TEST_PRETTY_PRINTING_JSON, expectedArrayTestMapNode()).loadTest(this.defaultPrinting(), this.prettyPrinting())
        ).flatMap(Function.identity());
    }

    /**
     * Gets the default {@link FileFormat}.
     *
     * @return the default {@link FileFormat}
     */
    protected abstract F defaultPrinting();

    /**
     * Gets the pretty printing {@link FileFormat}.
     *
     * @return the pretty printing {@link FileFormat}
     */
    protected abstract F prettyPrinting();
}
