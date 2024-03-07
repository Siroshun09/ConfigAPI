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

import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class EnumSerializationTest {

    @ParameterizedTest
    @MethodSource("testCases")
    <R extends Record> void test(RecordTestCase<R> testCase) {
        testCase.testDefaultSerializers();
        testCase.testDefaultDeserializers();
    }

    private static Stream<RecordTestCase<?>> testCases() {
        return Stream.of(
                RecordTestCase.create(
                        new EnumRecord(ExampleEnum.A, ExampleEnum.B, ExampleEnum.C),
                        expectedMapNode -> {
                            expectedMapNode.set("normal", ExampleEnum.A);
                            expectedMapNode.set("uppercase", ExampleEnum.B);
                            expectedMapNode.set("lowercase", ExampleEnum.C);
                        },
                        deserializingMapNode -> {
                            deserializingMapNode.set("normal", ExampleEnum.A);
                            deserializingMapNode.set("uppercase", "B");
                            deserializingMapNode.set("lowercase", "c");
                        }
                ),
                RecordTestCase.create(
                        new EnumList(List.of(ExampleEnum.A, ExampleEnum.B, ExampleEnum.C)),
                        expectedMapNode -> {
                            var list = expectedMapNode.createList("list");
                            list.add(ExampleEnum.A);
                            list.add(ExampleEnum.B);
                            list.add(ExampleEnum.C);
                        },
                        deserializingMapNode -> {
                            var list = deserializingMapNode.createList("list");
                            list.add(ExampleEnum.A);
                            list.add("B");
                            list.add("c");
                        }
                )
        );
    }

    private enum ExampleEnum {
        A,
        B,
        C
    }

    private record EnumRecord(ExampleEnum normal, ExampleEnum uppercase, ExampleEnum lowercase) {
    }

    private record EnumList(@CollectionType(ExampleEnum.class) List<ExampleEnum> list) {
    }
}
