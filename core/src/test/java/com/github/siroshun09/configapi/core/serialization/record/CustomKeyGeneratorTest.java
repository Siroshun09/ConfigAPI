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
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultString;
import com.github.siroshun09.configapi.core.serialization.key.Key;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

class CustomKeyGeneratorTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void test(@NotNull CustomKeyGeneratorTest.TestCase base) {
        var expectedMapNode = MapNode.create();
        base.expectedMapNodeBuilder().accept(expectedMapNode);

        testRecordSerializer(expectedMapNode, RecordSerializer.create(base.keyGenerator()));
        testRecordSerializer(expectedMapNode, RecordSerializer.<SampleRecord>builder().keyGenerator(base.keyGenerator()).build());

        testRecordDeserializer(expectedMapNode, RecordDeserializer.create(SampleRecord.class, base.keyGenerator()));
        testRecordDeserializer(expectedMapNode, RecordDeserializer.create(EXPECTED_RECORD, base.keyGenerator()));
        testRecordDeserializer(expectedMapNode, RecordDeserializer.builder(SampleRecord.class).keyGenerator(base.keyGenerator()).build());
        testRecordDeserializer(expectedMapNode, RecordDeserializer.builder(EXPECTED_RECORD).keyGenerator(base.keyGenerator()).build());

        testSerialization(expectedMapNode, RecordSerialization.create(SampleRecord.class, base.keyGenerator()));
        testSerialization(expectedMapNode, RecordSerialization.create(EXPECTED_RECORD, base.keyGenerator()));
        testSerialization(expectedMapNode, RecordSerialization.builder(SampleRecord.class).keyGenerator(base.keyGenerator()).build());
        testSerialization(expectedMapNode, RecordSerialization.builder(EXPECTED_RECORD).keyGenerator(base.keyGenerator()).build());
    }

    private static void testRecordSerializer(@NotNull MapNode mapNode, @NotNull RecordSerializer<SampleRecord> serializer) {
        NodeAssertion.assertEquals(mapNode, serializer.serialize(EXPECTED_RECORD));
        NodeAssertion.assertEquals(mapNode, serializer.serializeDefault(SampleRecord.class));
    }

    private static void testRecordDeserializer(@NotNull MapNode mapNode, @NotNull RecordDeserializer<SampleRecord> deserializer) {
        Assertions.assertEquals(EXPECTED_RECORD, deserializer.deserialize(mapNode));
        Assertions.assertEquals(EXPECTED_RECORD, deserializer.deserialize(MapNode.empty()));
    }

    private static void testSerialization(@NotNull MapNode mapNode, @NotNull RecordSerialization<SampleRecord> serialization) {
        testRecordSerializer(mapNode, serialization.serializer());
        testRecordDeserializer(mapNode, serialization.deserializer());
    }

    private record TestCase(@NotNull KeyGenerator keyGenerator,
                            @NotNull Consumer<MapNode> expectedMapNodeBuilder) {
    }

    private record SampleRecord(@DefaultString("a") String thisIsString,
                                @Key("custom-key") @DefaultString("b") String customKeyedString) {
    }

    private static final SampleRecord EXPECTED_RECORD = new SampleRecord("a", "b");

    private static Stream<TestCase> testCases() {
        return Stream.of(
                new TestCase(
                        KeyGenerator.AS_IS,
                        mapNode -> {
                            mapNode.set("thisIsString", "a");
                            mapNode.set("custom-key", "b");
                        }
                ),
                new TestCase(
                        KeyGenerator.CAMEL_TO_KEBAB,
                        mapNode -> {
                            mapNode.set("this-is-string", "a");
                            mapNode.set("custom-key", "b");
                        }
                ),
                new TestCase(
                        KeyGenerator.CAMEL_TO_SNAKE,
                        mapNode -> {
                            mapNode.set("this_is_string", "a");
                            mapNode.set("custom-key", "b");
                        }
                )
        );
    }
}
