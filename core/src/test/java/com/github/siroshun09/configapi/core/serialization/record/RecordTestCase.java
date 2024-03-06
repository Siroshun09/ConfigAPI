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

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.function.Consumer;

record RecordTestCase<R extends Record>(@NotNull R expectedRecord, @NotNull MapNode expectedMapNode,
                                        @NotNull Class<R> recordClass) {

    @SuppressWarnings("unchecked")
    static <R extends Record> @NotNull RecordTestCase<R> create(@NotNull R expectedRecord, @NotNull Consumer<MapNode> expectedMapNodeBuilder) {
        var mapNode = MapNode.create();
        expectedMapNodeBuilder.accept(mapNode);
        return new RecordTestCase<>(expectedRecord, mapNode.asView(), (Class<R>) expectedRecord.getClass());
    }

    void testDefaults() {
        this.testDefaultSerializers();
        this.testDefaultDeserializers();
        this.testDefaultSerializations();
    }

    void testSerialize(@NotNull RecordSerializer<? super R> serializer) {
        NodeAssertion.assertEquals(this.expectedMapNode, serializer.serialize(this.expectedRecord));
    }

    @SafeVarargs
    final void testSerialize(@NotNull RecordSerializer<? super R> @NotNull ... serializers) {
        for (var serializer : serializers) {
            this.testSerialize(serializer);
        }
    }

    void testDefaultSerializers() {
        this.testSerialize(
                RecordSerializer.serializer(),
                RecordSerializer.<R>builder().build()
        );
    }

    void testDeserialize(@NotNull RecordDeserializer<? extends R> deserializer) {
        Assertions.assertEquals(this.expectedRecord, deserializer.deserialize(this.expectedMapNode));
    }

    @SafeVarargs
    final void testDeserialize(@NotNull RecordDeserializer<? extends R> @NotNull ... deserializers) {
        for (var deserializer : deserializers) {
            this.testDeserialize(deserializer);
        }
    }

    void testDefaultDeserializers() {
        this.testDeserialize(
                RecordDeserializer.create(this.recordClass),
                RecordDeserializer.create(this.expectedRecord),
                RecordDeserializer.builder(this.recordClass).build(),
                RecordDeserializer.builder(this.expectedRecord).build()
        );
    }

    void testSerialization(@NotNull RecordSerialization<R> serialization) {
        this.testSerialize(serialization.serializer());
        this.testDeserialize(serialization.deserializer());
    }

    @SafeVarargs
    final void testSerialization(@NotNull RecordSerialization<R> @NotNull ... serializations) {
        for (var serialization : serializations) {
            this.testSerialization(serialization);
        }
    }

    void testDefaultSerializations() {
        this.testSerialization(
                RecordSerialization.create(this.recordClass),
                RecordSerialization.create(this.expectedRecord),
                RecordSerialization.builder(this.recordClass).build(),
                RecordSerialization.builder(this.expectedRecord).build()
        );
    }
}
