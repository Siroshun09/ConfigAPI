package dev.siroshun.configapi.codec;

import dev.siroshun.codec4j.api.error.DecodeError;
import dev.siroshun.codec4j.api.error.EncodeError;
import dev.siroshun.codec4j.io.Memory;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteValue;
import dev.siroshun.configapi.core.node.DoubleValue;
import dev.siroshun.configapi.core.node.FloatValue;
import dev.siroshun.configapi.core.node.IntValue;
import dev.siroshun.configapi.core.node.LongValue;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.test.shared.util.NodeAssertion;
import dev.siroshun.jfun.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static dev.siroshun.configapi.test.shared.util.NodeFactory.mapNode;

class NodeCodecTest {

    @Test
    void test() {
        MapNode node = mapNode(mapNode -> {
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
            mapNode.set("list", List.of("a", "b", "c"));
            mapNode.set("map", Map.of("key", "value"));
        });

        node.set("nested-map", node);

        Result<Memory, EncodeError> encodeResult = NodeCodec.MAP_NODE_CODEC.encode(Memory.out(), node);
        Assertions.assertTrue(encodeResult.isSuccess());

        Result<Node<?>, DecodeError> decodeResult = NodeCodec.NODE_CODEC.decode(encodeResult.unwrap());
        Assertions.assertTrue(decodeResult.isSuccess());
        NodeAssertion.assertEquals(node, decodeResult.unwrap());
    }
}
