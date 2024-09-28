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

import dev.siroshun.configapi.core.serialization.annotation.CollectionType;
import dev.siroshun.configapi.core.serialization.annotation.MapType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static dev.siroshun.configapi.core.serialization.record.RecordTestCase.create;

class RecordSerializeTest {

    @ParameterizedTest
    @MethodSource("testCases")
    <R extends Record> void testSerializeAndDeserialize(@NotNull RecordTestCase<R> testCase) {
        testCase.testDefaults();
    }

    private static @NotNull Stream<RecordTestCase<?>> testCases() {
        return Stream.of(
                create(
                        new BasicValues(
                                true, (byte) 10, 'a', 3.14, 6.28f, 20, 30L, (short) 40,
                                Boolean.TRUE, (byte) 50, 'b', 0.33, 0.66f, 60, 70L, (short) 80,
                                "test", ExampleEnum.B
                        ), mapNode -> {
                            mapNode.set("booleanValue", true);
                            mapNode.set("byteValue", (byte) 10);
                            mapNode.set("charValue", 'a');
                            mapNode.set("doubleValue", 3.14);
                            mapNode.set("floatValue", 6.28f);
                            mapNode.set("intValue", 20);
                            mapNode.set("longValue", 30L);
                            mapNode.set("shortValue", (short) 40);
                            mapNode.set("wrappedBooleanValue", Boolean.TRUE);
                            mapNode.set("wrappedByteValue", (byte) 50);
                            mapNode.set("wrappedCharValue", 'b');
                            mapNode.set("wrappedDoubleValue", 0.33);
                            mapNode.set("wrappedFloatValue", 0.66f);
                            mapNode.set("wrappedIntValue", 60);
                            mapNode.set("wrappedLongValue", 70L);
                            mapNode.set("wrappedShortValue", (short) 80);
                            mapNode.set("stringValue", "test");
                            mapNode.set("enumValue", ExampleEnum.B);
                        }
                ),
                create(
                        new CollectionRecord(
                                List.of("a", "b", "c"),
                                List.of("d", "e", "f"),
                                new LinkedHashSet<>(List.of("g", "h", "i")),
                                Map.of("key", "value", "left", "right")
                        ),
                        mapNode -> {
                            mapNode.set("collection", List.of("a", "b", "c"));
                            mapNode.set("list", List.of("d", "e", "f"));
                            mapNode.set("set", List.of("g", "h", "i"));
                            mapNode.set("map", Map.of("key", "value", "left", "right"));
                        }
                ),
                create(
                        new ArrayRecord(
                                new boolean[]{true, false, true},
                                new byte[]{(byte) 1, (byte) 2, (byte) 3},
                                new char[]{'a', 'b', 'c'},
                                new double[]{0.1, 0.2, 0.3},
                                new float[]{0.4f, 0.5f, 0.6f},
                                new int[]{4, 5, 6},
                                new long[]{7L, 8L, 9L},
                                new short[]{(short) 10, (short) 11, (short) 12},
                                new String[]{"a", "b", "c"},
                                new ExampleEnum[]{ExampleEnum.A, ExampleEnum.C}
                        ),
                        mapNode -> {
                            mapNode.set("booleanArray", new boolean[]{true, false, true});
                            mapNode.set("byteArray", new byte[]{(byte) 1, (byte) 2, (byte) 3});
                            mapNode.set("charArray", new char[]{'a', 'b', 'c'});
                            mapNode.set("doubleArray", new double[]{0.1, 0.2, 0.3});
                            mapNode.set("floatArray", new float[]{0.4f, 0.5f, 0.6f});
                            mapNode.set("intArray", new int[]{4, 5, 6});
                            mapNode.set("longArray", new long[]{7L, 8L, 9L});
                            mapNode.set("shortArray", new short[]{(short) 10, (short) 11, (short) 12});
                            mapNode.set("stringArray", new String[]{"a", "b", "c"});
                            mapNode.set("enumArray", new ExampleEnum[]{ExampleEnum.A, ExampleEnum.C});
                        }
                ),
                create(
                        new NestedRecord(
                                new StringPair("key", "value"),
                                new StringPair("left", "right")
                        ),
                        mapNode -> {
                            var pair1 = mapNode.createMap("pair1");
                            pair1.set("left", "key");
                            pair1.set("right", "value");

                            var pair2 = mapNode.createMap("pair2");
                            pair2.set("left", "left");
                            pair2.set("right", "right");
                        }
                )
        );
    }

    private enum ExampleEnum {
        A,
        B,
        C
    }

    private record BasicValues(
            boolean booleanValue,
            byte byteValue,
            char charValue,
            double doubleValue,
            float floatValue,
            int intValue,
            long longValue,
            short shortValue,
            Boolean wrappedBooleanValue,
            Byte wrappedByteValue,
            Character wrappedCharValue,
            Double wrappedDoubleValue,
            Float wrappedFloatValue,
            Integer wrappedIntValue,
            Long wrappedLongValue,
            Short wrappedShortValue,
            String stringValue,
            ExampleEnum enumValue
    ) {
    }

    private record CollectionRecord(
            @CollectionType(String.class) Collection<String> collection,
            @CollectionType(String.class) List<String> list,
            @CollectionType(String.class) Set<String> set,
            @MapType(key = String.class, value = String.class) Map<String, String> map
    ) {

        @Override
        public boolean equals(Object o) {
            if (o instanceof CollectionRecord other) {
                return this.collection.size() == other.collection.size() && this.collection.containsAll(other.collection) && other.collection.containsAll(this.collection) &&
                        this.list.equals(other.list) &&
                        this.set.size() == other.set.size() && this.set.containsAll(other.set) && other.set.containsAll(this.set) &&
                        this.map.equals(other.map);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.collection, this.list, this.set, this.map);
        }
    }

    private record ArrayRecord(
            boolean[] booleanArray,
            byte[] byteArray,
            char[] charArray,
            double[] doubleArray,
            float[] floatArray,
            int[] intArray,
            long[] longArray,
            short[] shortArray,
            String[] stringArray,
            ExampleEnum[] enumArray
    ) {
        @Override
        public boolean equals(Object o) {
            if (o instanceof ArrayRecord that) {
                return Arrays.equals(this.booleanArray, that.booleanArray) &&
                        Arrays.equals(this.byteArray, that.byteArray) &&
                        Arrays.equals(this.charArray, that.charArray) &&
                        Arrays.equals(this.doubleArray, that.doubleArray) &&
                        Arrays.equals(this.floatArray, that.floatArray) &&
                        Arrays.equals(this.intArray, that.intArray) &&
                        Arrays.equals(this.longArray, that.longArray) &&
                        Arrays.equals(this.shortArray, that.shortArray) &&
                        Arrays.equals(this.stringArray, that.stringArray) &&
                        Arrays.equals(this.enumArray, that.enumArray);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(this.booleanArray);
            result = 31 * result + Arrays.hashCode(this.byteArray);
            result = 31 * result + Arrays.hashCode(this.charArray);
            result = 31 * result + Arrays.hashCode(this.doubleArray);
            result = 31 * result + Arrays.hashCode(this.floatArray);
            result = 31 * result + Arrays.hashCode(this.intArray);
            result = 31 * result + Arrays.hashCode(this.longArray);
            result = 31 * result + Arrays.hashCode(this.shortArray);
            result = 31 * result + Arrays.hashCode(this.stringArray);
            return result;
        }
    }

    private record StringPair(String left, String right) {
    }

    private record NestedRecord(StringPair pair1, StringPair pair2) {
    }
}
