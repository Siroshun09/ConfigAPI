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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.serialization.Serialization;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import com.github.siroshun09.configapi.core.serialization.registry.SerializationRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

class CustomSerializerTest {

    private static final Serialization<UUID, Node<?>> UUID_SERIALIZATION = Serialization.create(
            uuid -> new LongArray(new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()}),
            node -> {
                if (node instanceof LongArray longArray) {
                    var array = longArray.value();
                    if (array.length != 2) {
                        throw new SerializationException("The length of the given array is not 2.");
                    }
                    return new UUID(array[0], array[1]);
                } else {
                    throw new SerializationException("Expected LongArray to deserialize it to UUID, but got " + node.getClass());
                }
            }
    );

    private static final SerializationRegistry<Node<?>> REGISTRY;

    static {
        REGISTRY = SerializationRegistry.create();
        REGISTRY.register(UUID.class, UUID_SERIALIZATION);
        REGISTRY.freeze();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    <R extends Record> void testSerializeCustomObject(RecordTestCase<R> testCase) {
        testCase.testSerialize(
                RecordSerializer.builder().addSerializer(UUID.class, UUID_SERIALIZATION.serializer()).build(),
                RecordSerializer.builder().addSerializers(REGISTRY.asSerializerRegistry()).build()
        );

        testCase.testDeserialize(
                RecordDeserializer.builder(testCase.recordClass()).addDeserializer(UUID.class, UUID_SERIALIZATION.deserializer()).build(),
                RecordDeserializer.builder(testCase.expectedRecord()).addDeserializer(UUID.class, UUID_SERIALIZATION.deserializer()).build(),

                RecordDeserializer.builder(testCase.recordClass()).addDeserializers(REGISTRY.asDeserializerRegistry()).build(),
                RecordDeserializer.builder(testCase.expectedRecord()).addDeserializers(REGISTRY.asDeserializerRegistry()).build()
        );

        testCase.testSerialization(
                RecordSerialization.builder(testCase.recordClass()).addSerialization(UUID.class, UUID_SERIALIZATION).build(),
                RecordSerialization.builder(testCase.expectedRecord()).addSerialization(UUID.class, UUID_SERIALIZATION).build(),

                RecordSerialization.builder(testCase.recordClass()).addSerialization(REGISTRY).build(),
                RecordSerialization.builder(testCase.expectedRecord()).addSerialization(REGISTRY).build(),

                RecordSerialization.builder(testCase.recordClass()).addSerializer(UUID.class, UUID_SERIALIZATION.serializer()).addDeserializer(UUID.class, UUID_SERIALIZATION.deserializer()).build(),
                RecordSerialization.builder(testCase.expectedRecord()).addSerializer(UUID.class, UUID_SERIALIZATION.serializer()).addDeserializer(UUID.class, UUID_SERIALIZATION.deserializer()).build(),

                RecordSerialization.builder(testCase.recordClass()).addSerializers(REGISTRY.asSerializerRegistry()).addDeserializers(REGISTRY.asDeserializerRegistry()).build(),
                RecordSerialization.builder(testCase.expectedRecord()).addSerializers(REGISTRY.asSerializerRegistry()).addDeserializers(REGISTRY.asDeserializerRegistry()).build()
        );
    }

    private static Stream<RecordTestCase<?>> testCases() {
        return Stream.of(
                RecordTestCase.create(
                        new UUIDRecord(new UUID(0, 0)),
                        mapNode -> mapNode.set("uuid", new LongArray(new long[]{0, 0}))
                ),
                RecordTestCase.create(
                        new UUIDList(List.of(new UUID(1, 1), new UUID(2, 2))),
                        mapNode -> {
                            var list = mapNode.createList("uuidList");
                            list.add(new LongArray(new long[]{1, 1}));
                            list.add(new LongArray(new long[]{2, 2}));
                        }
                ),
                RecordTestCase.create(
                        new UUIDArray(new UUID[]{new UUID(3, 3), new UUID(4, 4)}),
                        mapNode -> {
                            var list = mapNode.createList("uuids");
                            list.add(new LongArray(new long[]{3, 3}));
                            list.add(new LongArray(new long[]{4, 4}));
                        }
                )
        );
    }

    private record UUIDRecord(UUID uuid) {
    }

    private record UUIDList(@CollectionType(UUID.class) List<UUID> uuidList) {
    }

    private record UUIDArray(UUID[] uuids) {
        @Override
        public boolean equals(Object o) {
            if (o instanceof UUIDArray that) {
                return Arrays.equals(this.uuids, that.uuids);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.uuids);
        }
    }
}
