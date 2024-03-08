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

import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultBoolean;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultByte;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultChar;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultDouble;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultEnum;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultField;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultFloat;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultInt;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultLong;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultMapKey;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultMethod;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultNull;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultShort;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultString;
import com.github.siroshun09.configapi.core.serialization.annotation.Inline;
import com.github.siroshun09.configapi.core.serialization.annotation.MapType;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.siroshun09.configapi.core.serialization.record.RecordTestCase.create;

class DefaultValueTest {

    @ParameterizedTest
    @MethodSource("testCases")
    <R extends Record> void testDefaultValues(@NotNull RecordTestCase<R> testCase) {
        testCase.testDefaultSerializers();
        testCase.testDefaultDeserializers();

        NodeAssertion.assertEquals(testCase.expectedMapNode(), RecordSerializer.serializer().serializeDefault(testCase.recordClass()));
    }

    private static <R extends Record> void testDeserializer(@NotNull R expectedRecord, @NotNull RecordDeserializer<? extends R> deserializer) {
        Assertions.assertEquals(expectedRecord, deserializer.deserialize(MapNode.empty()));
    }

    private static Stream<RecordTestCase<?>> testCases() {
        return Stream.of(
                create(
                        new DefaultPrimitiveValues(true, (byte) 10, 'a', 3.14, 3.14f, 10, 10L, (short) 10),
                        mapNode -> {
                            mapNode.set("booleanValue", true);
                            mapNode.set("byteValue", NumberValue.fromNumber((byte) 10));
                            mapNode.set("charValue", new CharValue('a'));
                            mapNode.set("doubleValue", 3.14);
                            mapNode.set("floatValue", 3.14f);
                            mapNode.set("intValue", 10);
                            mapNode.set("longValue", 10L);
                            mapNode.set("shortValue", 10);
                        }
                ),
                create(
                        new DefaultStringValue("test"),
                        mapNode -> mapNode.set("value", "test")
                ),
                create(
                        new DefaultEnumValue(ExampleEnum.B),
                        mapNode -> mapNode.set("enumValue", ExampleEnum.B)
                ),
                create(
                        new MapWithDefaultKey(Map.of("default", new DefaultMapValue("test", 10))),
                        mapNode -> {
                            var mapValueNode = mapNode.createMap("map").createMap("default");
                            mapValueNode.set("string", "test");
                            mapValueNode.set("number", 10);
                        }
                ),
                create(
                        new ImplicitlyDefaultValues(
                                false,
                                (byte) 0,
                                Character.MIN_VALUE,
                                0.0,
                                0.0f,
                                0,
                                0L,
                                (short) 0,
                                false,
                                (byte) 0,
                                Character.MIN_VALUE,
                                0.0,
                                0.0f,
                                0,
                                0L,
                                (short) 0,
                                "",
                                null,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptySet(),
                                Collections.emptyMap()
                        ),
                        mapNode -> {
                            mapNode.set("booleanValue", false);
                            mapNode.set("byteValue", 0);
                            mapNode.set("charValue", Character.MIN_VALUE);
                            mapNode.set("doubleValue", 0);
                            mapNode.set("floatValue", 0);
                            mapNode.set("intValue", 0);
                            mapNode.set("longValue", 0);
                            mapNode.set("shortValue", 0);
                            mapNode.set("wrappedBooleanValue", false);
                            mapNode.set("wrappedByteValue", 0);
                            mapNode.set("wrappedCharValue",  Character.MIN_VALUE);
                            mapNode.set("wrappedDoubleValue", 0);
                            mapNode.set("wrappedFloatValue", 0);
                            mapNode.set("wrappedIntValue", 0);
                            mapNode.set("wrappedLongValue", 0);
                            mapNode.set("wrappedShortValue", 0);
                            mapNode.set("stringValue", "");
                            // enumValue is null, so it is not stored to MapNode
                            mapNode.createList("collection");
                            mapNode.createList("list");
                            mapNode.createList("set");
                            mapNode.createMap("map");
                        }
                ),
                create(
                        new ImplicitlyDefaultArrays(
                                new boolean[0],
                                new byte[0],
                                new char[0],
                                new double[0],
                                new float[0],
                                new int[0],
                                new long[0],
                                new short[0],
                                new String[0]
                        ),
                        mapNode -> {
                            mapNode.set("booleanArray", new boolean[0]);
                            mapNode.set("byteArray", new byte[0]);
                            mapNode.set("charArray", new char[0]);
                            mapNode.set("doubleArray", new double[0]);
                            mapNode.set("floatArray", new float[0]);
                            mapNode.set("intArray", new int[0]);
                            mapNode.set("longArray", new long[0]);
                            mapNode.set("shortArray", new short[0]);
                            mapNode.set("stringArray", new String[0]);
                        }
                ),
                create(
                        new DefaultNullValues(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                        mapNode -> { // All values are null, so they are not stored to MapNode
                        }
                ),
                create(
                        new DefaultStringValueByFieldAndMethod("field", "method"),
                        mapNode -> {
                            mapNode.set("defaultByField", "field");
                            mapNode.set("defaultByMethod", "method");
                        }
                ),
                create(
                        new DefaultNullStringValueByFieldAndMethod(null, null),
                        mapNode -> { // All values are null, so they are not stored to MapNode
                        }
                ),
                create(
                        new InlinedDefaultValueByField(new StringPair("key", "value")),
                        mapNode -> {
                            mapNode.set("left", "key");
                            mapNode.set("right", "value");
                        }
                ),
                create(
                        new InlinedDefaultValueByMethod(new StringPair("key", "value")),
                        mapNode -> {
                            mapNode.set("left", "key");
                            mapNode.set("right", "value");
                        }
                )
        );
    }

    private record DefaultPrimitiveValues(
            @DefaultBoolean(true) boolean booleanValue,
            @DefaultByte(10) byte byteValue,
            @DefaultChar('a') char charValue,
            @DefaultDouble(3.14) double doubleValue,
            @DefaultFloat(3.14f) float floatValue,
            @DefaultInt(10) int intValue,
            @DefaultLong(10) long longValue,
            @DefaultShort(10) short shortValue
    ) {
    }

    private record DefaultStringValue(@DefaultString("test") String value) {
    }

    private enum ExampleEnum {
        A,
        B,
        C
    }

    private record DefaultEnumValue(@DefaultEnum("B") ExampleEnum enumValue) {
    }

    private record DefaultMapValue(@DefaultString("test") String string, @DefaultInt(10) int number) {
    }

    private record MapWithDefaultKey(
            @DefaultMapKey("default") @MapType(key = String.class, value = DefaultMapValue.class) Map<String, DefaultMapValue> map
    ) {
    }

    private record ImplicitlyDefaultValues(
            boolean booleanValue, // false
            byte byteValue, // 0
            char charValue, // Character.MIN_VALUE
            double doubleValue, // 0.0
            float floatValue, // 0.0f
            int intValue, // 0
            long longValue, // 0
            short shortValue, // 0
            Boolean wrappedBooleanValue, // false (Boolean.FALSE)
            Byte wrappedByteValue, // 0
            Character wrappedCharValue, // Character.MIN_VALUE
            Double wrappedDoubleValue, // 0.0
            Float wrappedFloatValue, // 0.0f
            Integer wrappedIntValue, // 0
            Long wrappedLongValue, // 0
            Short wrappedShortValue, // 0
            String stringValue, // empty string ("")
            ExampleEnum enumValue, // null
            @CollectionType(String.class) Collection<String> collection, // empty list
            @CollectionType(String.class) List<String> list, // empty list
            @CollectionType(String.class) Set<String> set, // empty set
            @MapType(key = String.class, value = String.class) Map<String, String> map // empty map
    ) {
    }

    private record ImplicitlyDefaultArrays(
            boolean[] booleanArray,
            byte[] byteArray,
            char[] charArray,
            double[] doubleArray,
            float[] floatArray,
            int[] intArray,
            long[] longArray,
            short[] shortArray,
            String[] stringArray
    ) {
        @Override
        public boolean equals(Object o) {
            if (o instanceof ImplicitlyDefaultArrays that) {
                return Arrays.equals(this.booleanArray, that.booleanArray) &&
                        Arrays.equals(this.byteArray, that.byteArray) &&
                        Arrays.equals(this.charArray, that.charArray) &&
                        Arrays.equals(this.doubleArray, that.doubleArray) &&
                        Arrays.equals(this.floatArray, that.floatArray) &&
                        Arrays.equals(this.intArray, that.intArray) &&
                        Arrays.equals(this.longArray, that.longArray) &&
                        Arrays.equals(this.shortArray, that.shortArray) &&
                        Arrays.equals(this.stringArray, that.stringArray);
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

    private record DefaultNullValues(
            @DefaultNull Boolean booleanValue,
            @DefaultNull Byte byteValue,
            @DefaultNull Character charValue,
            @DefaultNull Double doubleValue,
            @DefaultNull Float floatValue,
            @DefaultNull Integer intValue,
            @DefaultNull Long longValue,
            @DefaultNull Short shortValue,
            @DefaultNull String stringValue,
            @DefaultNull ExampleEnum enumValue,
            @DefaultNull @CollectionType(String.class) Collection<String> collection,
            @DefaultNull @CollectionType(String.class) List<String> list,
            @DefaultNull @CollectionType(String.class) Set<String> set,
            @DefaultNull @MapType(key = String.class, value = String.class) Map<String, String> map,
            @DefaultNull boolean[] booleanArray,
            @DefaultNull byte[] byteArray,
            @DefaultNull char[] charArray,
            @DefaultNull double[] doubleArray,
            @DefaultNull float[] floatArray,
            @DefaultNull int[] intArray,
            @DefaultNull long[] longArray,
            @DefaultNull short[] shortArray,
            @DefaultNull String[] stringArray
    ) {
    }

    private record DefaultStringValueByFieldAndMethod(
            @DefaultField(clazz = DefaultStringValueByFieldAndMethod.class, name = "DEFAULT_VALUE") String defaultByField,
            @DefaultMethod(clazz = DefaultStringValueByFieldAndMethod.class, name = "defaultValue") String defaultByMethod
    ) {

        private static final String DEFAULT_VALUE = "field";

        private static String defaultValue() {
            return "method";
        }
    }

    private record DefaultNullStringValueByFieldAndMethod(
            @DefaultField(clazz = DefaultNullStringValueByFieldAndMethod.class, name = "DEFAULT_VALUE") String defaultByField,
            @DefaultMethod(clazz = DefaultNullStringValueByFieldAndMethod.class, name = "defaultValue") String defaultByMethod
    ) {

        private static final String DEFAULT_VALUE = null;

        private static String defaultValue() {
            return null;
        }
    }

    private record StringPair(String left, String right) {
    }

    private record InlinedDefaultValueByField(
            @Inline @DefaultField(clazz = InlinedDefaultValueByField.class, name = "DEFAULT_PAIR") StringPair pair
    ) {
        private static final StringPair DEFAULT_PAIR = new StringPair("key", "value");
    }

    private record InlinedDefaultValueByMethod(
            @Inline @DefaultMethod(clazz = InlinedDefaultValueByMethod.class, name = "defaultPair") StringPair pair
    ) {
        private static @NotNull StringPair defaultPair() {
            return new StringPair("key", "value");
        }
    }

    @Test
    void testWrongDefaultObjectType() {
        Assertions.assertThrows(SerializationException.class, () -> RecordDeserializer.create(WrongDefaultObjectType.class).deserialize(MapNode.empty()));
    }

    private record WrongDefaultObjectType(
            @DefaultField(clazz = WrongDefaultObjectType.class, name = "DEFAULT_VALUE") String defaultByField,
            @DefaultMethod(clazz = WrongDefaultObjectType.class, name = "defaultValue") String defaultByMethod
    ) {

        private static final int DEFAULT_VALUE = 1;

        private static int defaultValue() {
            return 1;
        }
    }

    @Test
    void testNotExistDefaultFieldAndMethod() {
        {
            var ex = Assertions.assertThrows(SerializationException.class, () -> RecordDeserializer.create(NotExistDefaultField.class).deserialize(MapNode.empty()));
            Assertions.assertInstanceOf(NoSuchFieldException.class, ex.getCause());
        }

        {
            var ex = Assertions.assertThrows(SerializationException.class, () -> RecordDeserializer.create(NotExistDefaultMethod.class).deserialize(MapNode.empty()));
            Assertions.assertInstanceOf(NoSuchMethodException.class, ex.getCause());
        }
    }

    private record NotExistDefaultField(
            @DefaultField(clazz = NotExistDefaultField.class, name = "DEFAULT_VALUE") String a
    ) {
    }

    private record NotExistDefaultMethod(
            @DefaultMethod(clazz = NotExistDefaultMethod.class, name = "defaultValue") String a
    ) {
    }
}
