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

package dev.siroshun.configapi.core.serialization.record;

import dev.siroshun.configapi.core.serialization.annotation.DefaultString;
import dev.siroshun.configapi.core.serialization.key.Key;
import dev.siroshun.configapi.core.serialization.key.KeyGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class CustomKeyGeneratorTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void test(@NotNull CustomKeyGeneratorTest.TestCase testCase) {
        testCase.testCase().testSerialize(
                RecordSerializer.create(testCase.keyGenerator()),
                RecordSerializer.<SampleRecord>builder().keyGenerator(testCase.keyGenerator()).build()
        );

        testCase.testCase().testDeserialize(
                RecordDeserializer.create(SampleRecord.class, testCase.keyGenerator()),
                RecordDeserializer.create(EXPECTED_RECORD, testCase.keyGenerator()),
                RecordDeserializer.builder(SampleRecord.class).keyGenerator(testCase.keyGenerator()).build(),
                RecordDeserializer.builder(EXPECTED_RECORD).keyGenerator(testCase.keyGenerator()).build()
        );

        testCase.testCase().testSerialization(
                RecordSerialization.create(SampleRecord.class, testCase.keyGenerator()),
                RecordSerialization.create(EXPECTED_RECORD, testCase.keyGenerator()),
                RecordSerialization.builder(SampleRecord.class).keyGenerator(testCase.keyGenerator()).build(),
                RecordSerialization.builder(EXPECTED_RECORD).keyGenerator(testCase.keyGenerator()).build()
        );
    }

    private record TestCase(@NotNull KeyGenerator keyGenerator,
                            @NotNull RecordTestCase<SampleRecord> testCase) {
    }

    private record SampleRecord(@DefaultString("a") String thisIsString,
                                @Key("custom-key") @DefaultString("b") String customKeyedString) {
    }

    private static final SampleRecord EXPECTED_RECORD = new SampleRecord("a", "b");

    private static Stream<TestCase> testCases() {
        return Stream.of(
                new TestCase(
                        KeyGenerator.AS_IS,
                        RecordTestCase.create(
                                EXPECTED_RECORD,
                                mapNode -> {
                                    mapNode.set("thisIsString", "a");
                                    mapNode.set("custom-key", "b");
                                }
                        )
                ),
                new TestCase(
                        KeyGenerator.CAMEL_TO_KEBAB,
                        RecordTestCase.create(
                                EXPECTED_RECORD,
                                mapNode -> {
                                    mapNode.set("this-is-string", "a");
                                    mapNode.set("custom-key", "b");
                                }
                        )
                ),
                new TestCase(
                        KeyGenerator.CAMEL_TO_SNAKE,
                        RecordTestCase.create(
                                EXPECTED_RECORD,
                                mapNode -> {
                                    mapNode.set("this_is_string", "a");
                                    mapNode.set("custom-key", "b");
                                }
                        )
                )
        );
    }
}
