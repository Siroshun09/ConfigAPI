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

package com.github.siroshun09.configapi.core.node;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueNodeTest {

    @Test
    void testBoolean() {
        testBooleanValue(BooleanValue.TRUE, as(Node.fromObject(true), BooleanValue.class));
        testBooleanValue(BooleanValue.FALSE, as(Node.fromObject(false), BooleanValue.class));

        testBooleanValue(BooleanValue.TRUE, as(Node.fromObject(Boolean.TRUE), BooleanValue.class));
        testBooleanValue(BooleanValue.FALSE, as(Node.fromObject(Boolean.FALSE), BooleanValue.class));
    }

    private void testBooleanValue(@NotNull BooleanValue expected, @NotNull BooleanValue actual) {
        Assertions.assertEquals(expected.asBoolean(), actual.asBoolean());
        Assertions.assertEquals(expected.booleanValue(), actual.booleanValue());
        Assertions.assertTrue(actual.hasValue());
        Assertions.assertSame(expected, actual);

        Assertions.assertSame(actual, Node.fromObject(actual));
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testByte() {
        testByteValue(new ByteValue((byte) 10), as(Node.fromObject((byte) 10), ByteValue.class));
        testByteValue(new ByteValue((byte) -10), as(Node.fromObject((byte) -10), ByteValue.class));

        testByteValue(new ByteValue((byte) 10), as(Node.fromObject(Byte.valueOf((byte) 10)), ByteValue.class));
        testByteValue(new ByteValue((byte) -10), as(Node.fromObject(Byte.valueOf((byte) -10)), ByteValue.class));
    }

    private void testByteValue(@NotNull ByteValue expected, @NotNull ByteValue actual) {
        Assertions.assertEquals(expected.asByte(), actual.asByte());
        Assertions.assertEquals(expected.byteValue(), actual.byteValue());
        testNumberValue(expected, actual);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testDouble() {
        testDoubleValue(new DoubleValue(3.14), as(Node.fromObject(3.14), DoubleValue.class));
        testDoubleValue(new DoubleValue(-3.14), as(Node.fromObject(-3.14), DoubleValue.class));

        testDoubleValue(new DoubleValue(3.14), as(Node.fromObject(Double.valueOf(3.14)), DoubleValue.class));
        testDoubleValue(new DoubleValue(-3.14), as(Node.fromObject(Double.valueOf(-3.14)), DoubleValue.class));
    }

    private void testDoubleValue(@NotNull DoubleValue expected, @NotNull DoubleValue actual) {
        Assertions.assertEquals(expected.asDouble(), actual.asDouble());
        Assertions.assertEquals(expected.doubleValue(), actual.doubleValue());
        testNumberValue(expected, actual);
    }

    @Test
    void testEnumValue() {
        testEnumValue(new EnumValue<>(ExampleEnum.A), as(Node.fromObject(ExampleEnum.A), EnumValue.class));
        testEnumValue(new EnumValue<>(ExampleEnum.B), as(Node.fromObject(ExampleEnum.B), EnumValue.class));
        testEnumValue(new EnumValue<>(ExampleEnum.C), as(Node.fromObject(ExampleEnum.C), EnumValue.class));
    }

    private void testEnumValue(@NotNull EnumValue<?> expected, @NotNull EnumValue<?> actual) {
        Assertions.assertSame(expected.value(), actual.value());
        Assertions.assertTrue(actual.hasValue());
        Assertions.assertEquals(expected, actual);

        Assertions.assertSame(actual, Node.fromObject(actual));
    }

    private enum ExampleEnum {
        A, B, C
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testFloat() {
        testFloatValue(new FloatValue(3.14f), as(Node.fromObject(3.14f), FloatValue.class));
        testFloatValue(new FloatValue(-3.14f), as(Node.fromObject(-3.14f), FloatValue.class));

        testFloatValue(new FloatValue(3.14f), as(Node.fromObject(Float.valueOf(3.14f)), FloatValue.class));
        testFloatValue(new FloatValue(-3.14f), as(Node.fromObject(Float.valueOf(-3.14f)), FloatValue.class));
    }

    private void testFloatValue(@NotNull FloatValue expected, @NotNull FloatValue actual) {
        Assertions.assertEquals(expected.asFloat(), actual.asFloat());
        Assertions.assertEquals(expected.floatValue(), actual.floatValue());
        testNumberValue(expected, actual);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testInt() {
        testIntValue(new IntValue(10), as(Node.fromObject(10), IntValue.class));
        testIntValue(new IntValue(-10), as(Node.fromObject(-10), IntValue.class));

        testIntValue(new IntValue(10), as(Node.fromObject(Integer.valueOf(10)), IntValue.class));
        testIntValue(new IntValue(-10), as(Node.fromObject(Integer.valueOf(-10)), IntValue.class));
    }

    private void testIntValue(@NotNull IntValue expected, @NotNull IntValue actual) {
        Assertions.assertEquals(expected.asInt(), actual.asInt());
        Assertions.assertEquals(expected.intValue(), actual.intValue());
        testNumberValue(expected, actual);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testLong() {
        testLongValue(new LongValue(10L), as(Node.fromObject(10L), LongValue.class));
        testLongValue(new LongValue(-10L), as(Node.fromObject(-10L), LongValue.class));

        testLongValue(new LongValue(10L), as(Node.fromObject(Long.valueOf(10L)), LongValue.class));
        testLongValue(new LongValue(-10L), as(Node.fromObject(Long.valueOf(-10L)), LongValue.class));
    }

    private void testLongValue(@NotNull LongValue expected, @NotNull LongValue actual) {
        Assertions.assertEquals(expected.asLong(), actual.asLong());
        Assertions.assertEquals(expected.longValue(), actual.longValue());
        testNumberValue(expected, actual);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    void testShort() {
        testShortValue(new ShortValue((short) 10), as(Node.fromObject((short) 10), ShortValue.class));
        testShortValue(new ShortValue((short) -10), as(Node.fromObject((short) -10), ShortValue.class));

        testShortValue(new ShortValue((short) 10), as(Node.fromObject(Short.valueOf((short) 10)), ShortValue.class));
        testShortValue(new ShortValue((short) -10), as(Node.fromObject(Short.valueOf((short) -10)), ShortValue.class));
    }

    private void testShortValue(@NotNull ShortValue expected, @NotNull ShortValue actual) {
        Assertions.assertEquals(expected.asShort(), actual.asShort());
        Assertions.assertEquals(expected.shortValue(), actual.shortValue());
        testNumberValue(expected, actual);
    }

    private static void testNumberValue(@NotNull NumberValue expected, @NotNull NumberValue actual) {
        Assertions.assertTrue(actual.hasValue());
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, expected.compareTo(actual));

        Assertions.assertSame(actual, Node.fromObject(actual));
    }

    @Test
    void testStringValue() {
        testStringValue(new StringValue("a"), as(Node.fromObject("a"), StringValue.class));
        testStringValue(StringValue.EMPTY, as(Node.fromObject(""), StringValue.class));
    }

    private void testStringValue(@NotNull StringValue expected, @NotNull StringValue actual) {
        Assertions.assertEquals(expected.value(), actual.value());
        Assertions.assertTrue(actual.hasValue());
        Assertions.assertEquals(expected, actual);

        Assertions.assertSame(actual, Node.fromObject(actual));
    }

    private <N extends Node<?>> N as(@NotNull Node<?> node, @NotNull Class<N> clazz) {
        return Assertions.assertInstanceOf(clazz, node);
    }
}
