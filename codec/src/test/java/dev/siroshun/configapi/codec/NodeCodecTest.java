package dev.siroshun.configapi.codec;

import dev.siroshun.codec4j.api.error.DecodeError;
import dev.siroshun.codec4j.api.error.EncodeError;
import dev.siroshun.codec4j.io.Memory;
import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.ByteValue;
import dev.siroshun.configapi.core.node.CharArray;
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
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.test.shared.util.NodeAssertion;
import dev.siroshun.jfun.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class NodeCodecTest {

    @ParameterizedTest
    @MethodSource("nodeCodecTestCases")
    void testNodeCodec(NodeCodecTestCase testCase) {
        Result<Memory, EncodeError> encodeResult = NodeCodec.NODE_CODEC.encode(Memory.out(), testCase.originalNode);
        Assertions.assertTrue(encodeResult.isSuccess());

        Result<Node<?>, DecodeError> decodeResult = NodeCodec.NODE_CODEC.decode(encodeResult.unwrap());
        Assertions.assertTrue(decodeResult.isSuccess());
        NodeAssertion.assertEquals(testCase.expectedNode, decodeResult.unwrap());
    }

    private static Stream<NodeCodecTestCase> nodeCodecTestCases() {
        return Stream.of(
                // Boolean tests
                new NodeCodecTestCase(new BooleanArray(new boolean[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new BooleanArray(new boolean[]{true}), ListNode.create(List.of(true))),
                new NodeCodecTestCase(new BooleanArray(new boolean[]{true, false}), ListNode.create(List.of(true, false))),
                new NodeCodecTestCase(new BooleanArray(new boolean[2]), ListNode.create(List.of(false, false))),
                new NodeCodecTestCase(new BooleanValue(true), new BooleanValue(true)),
                new NodeCodecTestCase(new BooleanValue(false), new BooleanValue(false)),

                // Byte tests
                new NodeCodecTestCase(new ByteArray(new byte[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new ByteArray(new byte[]{1}), ListNode.create(List.of((byte) 1))),
                new NodeCodecTestCase(new ByteArray(new byte[]{1, 2, 3}), ListNode.create(List.of((byte) 1, (byte) 2, (byte) 3))),
                new NodeCodecTestCase(new ByteArray(new byte[2]), ListNode.create(List.of((byte) 0, (byte) 0))),
                new NodeCodecTestCase(new ByteValue((byte) 0), new ByteValue((byte) 0)),
                new NodeCodecTestCase(new ByteValue((byte) 1), new ByteValue((byte) 1)),
                new NodeCodecTestCase(new ByteValue(Byte.MIN_VALUE), new ByteValue(Byte.MIN_VALUE)),
                new NodeCodecTestCase(new ByteValue(Byte.MAX_VALUE), new ByteValue(Byte.MAX_VALUE)),

                // Char tests
                new NodeCodecTestCase(new CharArray(new char[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new CharArray(new char[]{'a'}), ListNode.create(List.of('a'))),
                new NodeCodecTestCase(new CharArray(new char[]{'a', 'b', 'c'}), ListNode.create(List.of('a', 'b', 'c'))),
                new NodeCodecTestCase(new CharArray(new char[2]), ListNode.create(List.of('\0', '\0'))),

                // Double tests
                new NodeCodecTestCase(new DoubleArray(new double[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new DoubleArray(new double[]{1.0}), ListNode.create(List.of(1.0))),
                new NodeCodecTestCase(new DoubleArray(new double[]{1.0, 2.0, 3.0}), ListNode.create(List.of(1.0, 2.0, 3.0))),
                new NodeCodecTestCase(new DoubleArray(new double[]{1.5, 2.5, 3.5}), ListNode.create(List.of(1.5, 2.5, 3.5))),
                new NodeCodecTestCase(new DoubleArray(new double[]{3.14, 2.718}), ListNode.create(List.of(3.14, 2.718))),
                new NodeCodecTestCase(new DoubleArray(new double[2]), ListNode.create(List.of(0.0, 0.0))),
                new NodeCodecTestCase(new DoubleValue(0.0), new DoubleValue(0.0)),
                new NodeCodecTestCase(new DoubleValue(1.0), new DoubleValue(1.0)),
                new NodeCodecTestCase(new DoubleValue(-1.0), new DoubleValue(-1.0)),
                new NodeCodecTestCase(new DoubleValue(3.14), new DoubleValue(3.14)),
                new NodeCodecTestCase(new DoubleValue(Double.MIN_VALUE), new DoubleValue(Double.MIN_VALUE)),
                new NodeCodecTestCase(new DoubleValue(Double.MAX_VALUE), new DoubleValue(Double.MAX_VALUE)),

                // Float tests
                new NodeCodecTestCase(new FloatArray(new float[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new FloatArray(new float[]{1.0f}), ListNode.create(List.of(1.0f))),
                new NodeCodecTestCase(new FloatArray(new float[]{1.0f, 2.0f, 3.0f}), ListNode.create(List.of(1.0f, 2.0f, 3.0f))),
                new NodeCodecTestCase(new FloatArray(new float[]{1.5f, 2.5f, 3.5f}), ListNode.create(List.of(1.5f, 2.5f, 3.5f))),
                new NodeCodecTestCase(new FloatArray(new float[]{3.14f, 2.718f}), ListNode.create(List.of(3.14f, 2.718f))),
                new NodeCodecTestCase(new FloatArray(new float[2]), ListNode.create(List.of(0.0f, 0.0f))),
                new NodeCodecTestCase(new FloatValue(0.0f), new FloatValue(0.0f)),
                new NodeCodecTestCase(new FloatValue(1.0f), new FloatValue(1.0f)),
                new NodeCodecTestCase(new FloatValue(-1.0f), new FloatValue(-1.0f)),
                new NodeCodecTestCase(new FloatValue(3.14f), new FloatValue(3.14f)),
                new NodeCodecTestCase(new FloatValue(Float.MIN_VALUE), new FloatValue(Float.MIN_VALUE)),
                new NodeCodecTestCase(new FloatValue(Float.MAX_VALUE), new FloatValue(Float.MAX_VALUE)),

                // Int tests
                new NodeCodecTestCase(new IntArray(new int[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new IntArray(new int[]{1}), ListNode.create(List.of(1))),
                new NodeCodecTestCase(new IntArray(new int[]{1, 2, 3}), ListNode.create(List.of(1, 2, 3))),
                new NodeCodecTestCase(new IntArray(new int[2]), ListNode.create(List.of(0, 0))),
                new NodeCodecTestCase(new IntValue(0), new IntValue(0)),
                new NodeCodecTestCase(new IntValue(1), new IntValue(1)),
                new NodeCodecTestCase(new IntValue(Integer.MIN_VALUE), new IntValue(Integer.MIN_VALUE)),
                new NodeCodecTestCase(new IntValue(Integer.MAX_VALUE), new IntValue(Integer.MAX_VALUE)),

                // Long tests
                new NodeCodecTestCase(new LongArray(new long[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new LongArray(new long[]{1L}), ListNode.create(List.of(1L))),
                new NodeCodecTestCase(new LongArray(new long[]{1L, 2L, 3L}), ListNode.create(List.of(1L, 2L, 3L))),
                new NodeCodecTestCase(new LongArray(new long[2]), ListNode.create(List.of(0L, 0L))),
                new NodeCodecTestCase(new LongValue(0L), new LongValue(0L)),
                new NodeCodecTestCase(new LongValue(1L), new LongValue(1L)),
                new NodeCodecTestCase(new LongValue(Long.MIN_VALUE), new LongValue(Long.MIN_VALUE)),
                new NodeCodecTestCase(new LongValue(Long.MAX_VALUE), new LongValue(Long.MAX_VALUE)),

                // Short tests
                new NodeCodecTestCase(new ShortArray(new short[0]), ListNode.create(List.of())),
                new NodeCodecTestCase(new ShortArray(new short[]{1}), ListNode.create(List.of((short) 1))),
                new NodeCodecTestCase(new ShortArray(new short[]{1, 2, 3}), ListNode.create(List.of((short) 1, (short) 2, (short) 3))),
                new NodeCodecTestCase(new ShortArray(new short[2]), ListNode.create(List.of((short) 0, (short) 0))),
                new NodeCodecTestCase(new ShortValue((short) 0), new ShortValue((short) 0)),
                new NodeCodecTestCase(new ShortValue((short) 1), new ShortValue((short) 1)),
                new NodeCodecTestCase(new ShortValue(Short.MIN_VALUE), new ShortValue(Short.MIN_VALUE)),
                new NodeCodecTestCase(new ShortValue(Short.MAX_VALUE), new ShortValue(Short.MAX_VALUE)),

                // String tests
                new NodeCodecTestCase(StringValue.fromString(""), StringValue.fromString("")),
                new NodeCodecTestCase(StringValue.fromString("test"), StringValue.fromString("test")),
                new NodeCodecTestCase(StringValue.fromString("a"), StringValue.fromString("a")),
                new NodeCodecTestCase(StringValue.fromString("テスト"), StringValue.fromString("テスト")),

                // Enum
                new NodeCodecTestCase(new EnumValue<>(TestEnum.A), StringValue.fromString("A")),
                new NodeCodecTestCase(new EnumValue<>(TestEnum.B), StringValue.fromString("B")),
                new NodeCodecTestCase(new EnumValue<>(TestEnum.C), StringValue.fromString("C")),

                // double -> int
                new NodeCodecTestCase(new DoubleValue(Integer.MIN_VALUE), new IntValue(Integer.MIN_VALUE)),
                new NodeCodecTestCase(new DoubleValue(Integer.MAX_VALUE), new IntValue(Integer.MAX_VALUE)),

                // double -> long
                new NodeCodecTestCase(new DoubleValue((double) Long.MIN_VALUE), new LongValue(Long.MIN_VALUE)),
                new NodeCodecTestCase(new DoubleValue((double) Long.MAX_VALUE), new LongValue(Long.MAX_VALUE))
        ).flatMap(testCase -> Stream.of(
                testCase,
                // Add cases for wrapped node by CommentedNode
                new NodeCodecTestCase(CommentableNode.withComment(testCase.originalNode, null), testCase.expectedNode)
        ));
    }

    private record NodeCodecTestCase(Node<?> originalNode, Node<?> expectedNode) {
    }

    private enum TestEnum {
        A, B, C
    }

    @Test
    void testNodeCodecUnsupportedNodeEncodeError() {
        Result<Memory, EncodeError> result = NodeCodec.NODE_CODEC.encode(Memory.out(), null);
        Assertions.assertTrue(result.isFailure());
        Assertions.assertEquals(new NodeCodec.UnsupportedNodeEncodeError(null), result.unwrapError());
    }

    @ParameterizedTest
    @MethodSource("listNodeCodecTestCases")
    void testListNodeCodec(ListNodeCodecTestCase testCase) {
        Result<Memory, EncodeError> encodeResult = NodeCodec.LIST_NODE_CODEC.encode(Memory.out(), testCase.originalNode);
        Assertions.assertTrue(encodeResult.isSuccess());

        Result<ListNode, DecodeError> decodeResult = NodeCodec.LIST_NODE_CODEC.decode(encodeResult.unwrap());
        Assertions.assertTrue(decodeResult.isSuccess());
        NodeAssertion.assertEquals(testCase.expectedNode, decodeResult.unwrap());
    }

    private static Stream<ListNodeCodecTestCase> listNodeCodecTestCases() {
        return Stream.of(
                expectSameListNode(ListNode.empty()),
                expectSameListNode(ListNode.create()),
                expectSameListNode(ListNode.create(5)),
                expectSameListNode(ListNode.create(List.of("test"))),
                expectSameListNode(ListNode.create(List.of("test1", "test2"))),
                expectSameListNode(ListNode.create(List.of("test", 1, 3.14, List.of("test1")))),
                expectSameListNode(ListNode.create(List.of(MapNode.create(Map.of("test", "test", "list", List.of("element"), "map", Map.of("test", "test"))))))
        );
    }

    private static ListNodeCodecTestCase expectSameListNode(ListNode original) {
        return new ListNodeCodecTestCase(original, original);
    }

    private record ListNodeCodecTestCase(ListNode originalNode, ListNode expectedNode) {
    }

    @ParameterizedTest
    @MethodSource("mapNodeCodecTestCases")
    void testMapNodeCodec(MapNodeCodecTestCase testCase) {
        Result<Memory, EncodeError> encodeResult = NodeCodec.MAP_NODE_CODEC.encode(Memory.out(), testCase.originalNode);
        Assertions.assertTrue(encodeResult.isSuccess());

        Result<MapNode, DecodeError> decodeResult = NodeCodec.MAP_NODE_CODEC.decode(encodeResult.unwrap());
        Assertions.assertTrue(decodeResult.isSuccess());
        NodeAssertion.assertEquals(testCase.expectedNode, decodeResult.unwrap());
    }

    private static Stream<MapNodeCodecTestCase> mapNodeCodecTestCases() {
        return Stream.of(
                expectSameMapNode(MapNode.empty()),
                expectSameMapNode(MapNode.create()),
                expectSameMapNode(MapNode.create(Map.of("key", "value"))),
                expectSameMapNode(MapNode.create(Map.of("key1", "value1", "key2", "value2"))),
                expectSameMapNode(MapNode.create(Map.of("string", "value", "int", 1, "double", 3.14))),
                expectSameMapNode(MapNode.create(Map.of("list", List.of("element1", "element2")))),
                expectSameMapNode(MapNode.create(Map.of("nestedMap", Map.of("nestedKey", "nestedValue")))),
                expectSameMapNode(MapNode.create(Map.of("complex", Map.of("list", List.of(1, 2, 3), "map", Map.of("key", "value")))))
        );
    }

    private static MapNodeCodecTestCase expectSameMapNode(MapNode original) {
        return new MapNodeCodecTestCase(original, original);
    }

    private record MapNodeCodecTestCase(MapNode originalNode, MapNode expectedNode) {
    }
}
