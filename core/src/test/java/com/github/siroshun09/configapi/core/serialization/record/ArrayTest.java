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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.Serialization;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ArrayTest {

    private static final int[] INT_ARRAY = {1, 2, 3};
    private static final byte[] BYTE_ARRAY = {1, 2, 3};
    private static final UUID[] UUID_ARRAY = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
    private static final Serialization<UUID, Node<?>> UUID_SERIALIZATION = Serialization.create(
            uuid -> StringValue.fromString(uuid.toString()),
            node -> node instanceof StringValue stringValue ? UUID.fromString(stringValue.value()) : null
    );

    private static final ArrayRecord EXPECTED_RECORD = new ArrayRecord(INT_ARRAY, BYTE_ARRAY, UUID_ARRAY);
    private static final MapNode EXPECTED_MAPNODE;

    static {
        var mapNode = MapNode.create();
        mapNode.set("intArray", INT_ARRAY);
        mapNode.set("byteArray", BYTE_ARRAY);
        mapNode.set("uuidArray", UUID_ARRAY);
        EXPECTED_MAPNODE = mapNode;
    }

    @Test
    void testSerializeAndDeserialize() {
        var serialization = this.createSerialization().build();
        var serialized = serialization.serializer().serialize(EXPECTED_RECORD);
        NodeAssertion.assertEquals(EXPECTED_MAPNODE, serialized);

        var deserialized = serialization.deserializer().deserialize(serialized);
        Assertions.assertArrayEquals(EXPECTED_RECORD.intArray, deserialized.intArray);
        Assertions.assertArrayEquals(EXPECTED_RECORD.byteArray, deserialized.byteArray);
        Assertions.assertArrayEquals(EXPECTED_RECORD.uuidArray, deserialized.uuidArray);
    }

    @Test
    void testDefaultRecord() {
        var serialization = this.createSerialization().defaultRecord(EXPECTED_RECORD).build();

        var deserialized = serialization.deserializer().deserialize(MapNode.empty());
        Assertions.assertArrayEquals(EXPECTED_RECORD.intArray, deserialized.intArray);
        Assertions.assertArrayEquals(EXPECTED_RECORD.byteArray, deserialized.byteArray);
        Assertions.assertArrayEquals(EXPECTED_RECORD.uuidArray, deserialized.uuidArray);
    }

    private @NotNull RecordSerialization.Builder<ArrayRecord> createSerialization() {
        return RecordSerialization.builder(ArrayRecord.class).addSerialization(UUID.class, UUID_SERIALIZATION);
    }

    private record ArrayRecord(int[] intArray, byte[] byteArray, UUID[] uuidArray) {
    }
}
