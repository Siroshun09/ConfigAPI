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

package dev.siroshun.configapi.format.properties;

import dev.siroshun.configapi.core.comment.SimpleComment;
import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.ByteValue;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.CommentableNode;
import dev.siroshun.configapi.core.node.DoubleArray;
import dev.siroshun.configapi.core.node.DoubleValue;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.FloatArray;
import dev.siroshun.configapi.core.node.FloatValue;
import dev.siroshun.configapi.core.node.IntArray;
import dev.siroshun.configapi.core.node.IntValue;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.LongArray;
import dev.siroshun.configapi.core.node.LongValue;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.ObjectNode;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringRepresentable;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.test.shared.file.TextFileFormatTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Writer;
import java.util.function.Function;
import java.util.stream.Stream;

import static dev.siroshun.configapi.test.shared.util.NodeFactory.mapNode;

class PropertiesFormatTest extends TextFileFormatTest<MapNode, PropertiesFormat> {

    @Override
    protected Stream<PropertiesFormat> fileFormats() {
        return Stream.of(PropertiesFormat.DEFAULT);
    }

    @Override
    protected @NotNull String extension() {
        return ".properties";
    }

    @Override
    protected @NotNull MapNode emptyNode() {
        return MapNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return true;
    }

    @Override
    protected Stream<TestCase<MapNode, PropertiesFormat>> testCases() {
        return Stream.of(
                testCase(
                        """
                                a=b
                                1=2\\=3
                                1\\=2=3
                                empty=
                                =empty
                                """,
                        mapNode(mapNode -> {
                            mapNode.set("a", "b");
                            mapNode.set("1", "2=3");
                            mapNode.set("1=2", "3");
                            mapNode.set("empty", "");
                            mapNode.set("", "empty");
                        })
                ).saveAndLoadTest(PropertiesFormat.DEFAULT),
                stringRepresentableNodes()
                        .flatMap(PropertiesFormatTest::asKeyOrValueOrBoth)
                        .flatMap(testCase -> testCase.saveAndLoadTest(PropertiesFormat.DEFAULT))
        ).flatMap(Function.identity());
    }

    private static Stream<Node<?>> stringRepresentableNodes() {
        return Stream.of(
                BooleanValue.TRUE,
                BooleanValue.FALSE,
                new ByteValue((byte) 1),
                new CharValue('a'),
                new DoubleValue(3.14),
                new EnumValue<>(SharedEnum.B),
                new FloatValue(3.14f),
                new IntValue(1),
                new LongValue(1L),
                new ShortValue((short) 1),
                StringValue.fromString("a")
        );
    }

    private static Stream<TestCaseBase<MapNode>> asKeyOrValueOrBoth(Node<?> node) {
        String name = node.getClass().getSimpleName();
        String string;

        if (node instanceof StringRepresentable stringRepresentable) {
            string = stringRepresentable.asString();
        } else {
            throw new IllegalArgumentException(name + " is not StringRepresentable");
        }

        return Stream.of(
                testCase(string + "=" + name + "\n", mapNode(mapNode -> mapNode.set(string, name))), // String represented node is used as key
                testCase(name + "=" + string + "\n", mapNode(mapNode -> mapNode.set(name, string))), // String represented node is used as value
                testCase(string + "=" + string + "\n", mapNode(mapNode -> mapNode.set(string, string))) // String represented node is used as both key and value
        );
    }

    @ParameterizedTest
    @MethodSource("nonStringRepresentableNodes")
    void testNonStringRepresentableNode(@NotNull Node<?> node) {
        var mapNode = MapNode.create();

        mapNode.set("invalid", node);
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertiesFormat.DEFAULT.save(mapNode, Writer.nullWriter()));

        mapNode.set("invalid", CommentableNode.withComment(node, SimpleComment.create("test")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertiesFormat.DEFAULT.save(mapNode, Writer.nullWriter()));
    }

    private static @NotNull Stream<Node<?>> nonStringRepresentableNodes() {
        return Stream.of(
                ListNode.create(),
                MapNode.create(),
                new ObjectNode<>(new Object()),
                new BooleanArray(new boolean[0]),
                new ByteArray(new byte[0]),
                new CharArray(new char[0]),
                new DoubleArray(new double[0]),
                new FloatArray(new float[0]),
                new IntArray(new int[0]),
                new LongArray(new long[0]),
                new ShortArray(new short[0])
        );
    }
}
