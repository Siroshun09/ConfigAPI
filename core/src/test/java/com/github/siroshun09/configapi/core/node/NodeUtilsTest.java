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

package com.github.siroshun09.configapi.core.node;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

class NodeUtilsTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void testToNode(TestCase testCase) {
        NodeAssertion.assertEquals(testCase.expectedNode(), NodeUtils.toNode(testCase.value()));
    }

    private static Stream<TestCase> testCases() {
        return Stream.of(
                testCase("a", StringValue.fromString("a")),
                testCase(true, BooleanValue.TRUE),
                testCase('a', new CharValue('a')),
                testCase((byte) 1, new ByteValue((byte) 1)),
                testCase(3.14, new DoubleValue(3.14)),
                testCase(3.14f, new FloatValue(3.14f)),
                testCase(1, new IntValue(1)),
                testCase(1L, new LongValue(1L)),
                testCase((short) 1, new ShortValue((short) 1)),
                testCase(ExampleEnum.A, new EnumValue<>(ExampleEnum.A)),
                listNode(),
                mapNode(),
                objectNode(),
                arrayNode(new boolean[]{true, false, true}, BooleanArray::new),
                arrayNode(new byte[]{(byte) -1, (byte) 0, (byte) 1}, ByteArray::new),
                arrayNode(new char[]{'a', 'b', 'c'}, CharArray::new),
                arrayNode(new double[]{-1.5, 0.0, 1.5}, DoubleArray::new),
                arrayNode(new float[]{-1.5f, 0.0f, 1.5f}, FloatArray::new),
                arrayNode(new int[]{-1, 0, 1}, IntArray::new),
                arrayNode(new long[]{-1L, 0L, 1L}, LongArray::new),
                arrayNode(new short[]{(short) -1, (short) 0, (short) 1}, ShortArray::new)
        ).flatMap(testCase -> Stream.of(
                testCase,
                testCase(testCase.expectedNode(), testCase.expectedNode())
        ));
    }

    private static TestCase listNode() {
        var expectedListNode = ListNode.create();
        Stream.of("a", "b", "c").forEach(expectedListNode::add);
        return testCase(List.of("a", "b", "c"), expectedListNode);
    }

    private static TestCase mapNode() {
        var map = new LinkedHashMap<String, String>();
        map.put("key_1", "value_1");
        map.put("key_2", "value_2");
        var expectedMapNode = MapNode.create();
        expectedMapNode.set("key_1", "value_1");
        expectedMapNode.set("key_2", "value_2");
        return testCase(map, expectedMapNode);
    }

    private static TestCase objectNode() {
        var object = new Object();
        return testCase(object, new ObjectNode<>(object));
    }

    private static <A> TestCase arrayNode(A array, Function<A, ? extends Node<?>> nodeFactory) {
        return testCase(array, nodeFactory.apply(array));
    }

    @Test
    void testCommentableNode() {
        var commentedValueNode = CommentableNode.withComment(StringValue.fromString("test"), COMMENT);
        NodeAssertion.assertEquals(commentedValueNode, NodeUtils.toNode(commentedValueNode));

        var commentedListNode = ListNode.create(List.of("a", "b", "c"));
        commentedListNode.setComment(COMMENT);
        var copiedListNode = NodeUtils.toNode(commentedListNode);
        NodeAssertion.assertEquals(commentedListNode, copiedListNode);
        Assertions.assertNotSame(commentedListNode, copiedListNode);

        var commentedMapNode = MapNode.create(Map.of("a", "b"));
        commentedMapNode.setComment(COMMENT);
        var copiedMapNode = NodeUtils.toNode(commentedMapNode);
        NodeAssertion.assertEquals(commentedMapNode, copiedMapNode);
        Assertions.assertNotSame(commentedMapNode, copiedMapNode);
    }

    private static TestCase testCase(Object value, Node<?> expectedNode) {
        return new TestCase(value, expectedNode);
    }

    private static final SimpleComment COMMENT = SimpleComment.create("test");

    private record TestCase(Object value, Node<?> expectedNode) {
    }

    private enum ExampleEnum {
        A, B, C
    }
}
