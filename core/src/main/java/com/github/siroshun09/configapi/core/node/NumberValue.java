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

package com.github.siroshun09.configapi.core.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An interface to represent that {@link Node} holds number.
 */
public sealed interface NumberValue extends ValueNode<Number> permits IntValue, LongValue, FloatValue, DoubleValue, ByteValue, ShortValue {

    /**
     * A {@link NumberValue} that represents zero.
     */
    NumberValue ZERO = new IntValue(0);

    /**
     * Creates {@link NumberValue} from the given {@link Number}.
     * <p>
     * If the given number is zero, this method returns {@link #ZERO}.
     * For other values, the appropriate value record (IntValue, DoubleValue, etc.) is used.
     * <p>
     * Supported Number types are as follows:
     *
     * <ul>
     *     <li>Classes that wraps primitive types (like {@link Integer}, {@link Double})</li>
     *     <li>{@link AtomicInteger}</li>
     *     <li>{@link AtomicLong}</li>
     * </ul>
     *
     * @param value the number value
     * @return a {@link NumberValue} with the given {@link Number}
     */
    static @NotNull NumberValue fromNumber(@Nullable Number value) {
        if (value instanceof Integer number) {
            return number == 0 ? NumberValue.ZERO : new IntValue(number);
        } else if (value instanceof Long number) {
            return number == 0 ? NumberValue.ZERO : new LongValue(number);
        } else if (value instanceof Float number) {
            return Float.compare(number, 0) == 0 ? NumberValue.ZERO : new FloatValue(number);
        } else if (value instanceof Double number) {
            return Double.compare(number, 0) == 0 ? NumberValue.ZERO : new DoubleValue(number);
        } else if (value instanceof Byte number) {
            return number == 0 ? NumberValue.ZERO : new ByteValue(number);
        } else if (value instanceof Short number) {
            return number == 0 ? NumberValue.ZERO : new ShortValue(number);
        } else if (value instanceof AtomicInteger number) {
            int v = number.get();
            return v == 0 ? NumberValue.ZERO : new IntValue(v);
        } else if (value instanceof AtomicLong number) {
            long v = number.get();
            return v == 0 ? NumberValue.ZERO : new LongValue(v);
        } else if (value == null) {
            return NumberValue.ZERO;
        } else {
            throw new IllegalArgumentException("Unsupported Number: " + value.getClass());
        }
    }

    @Override
    default boolean hasValue() {
        return true;
    }

    /**
     * Gets the number as int.
     *
     * @return the int value
     */
    int asInt();

    /**
     * Gets the number as long.
     *
     * @return the long value
     */
    long asLong();

    /**
     * Gets the number as float.
     *
     * @return the float value
     */
    float asFloat();

    /**
     * Gets the number as double.
     *
     * @return the double value
     */
    double asDouble();

    /**
     * Gets the number as byte.
     *
     * @return the byte value
     */
    byte asByte();

    /**
     * Gets the number as short.
     *
     * @return the short value
     */
    short asShort();
}
