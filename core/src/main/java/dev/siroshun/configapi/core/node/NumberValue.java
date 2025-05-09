/*
 *     Copyright 2025 Siroshun09
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

package dev.siroshun.configapi.core.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An interface to represent that {@link Node} holds number.
 */
public sealed interface NumberValue extends ValueNode<Number>, Comparable<NumberValue> permits IntValue, LongValue, FloatValue, DoubleValue, ByteValue, ShortValue {

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
        switch (value) {
            case Integer number -> {
                return number == 0 ? NumberValue.ZERO : new IntValue(number);
            }
            case Long number -> {
                return number == 0 ? NumberValue.ZERO : new LongValue(number);
            }
            case Float number -> {
                return Float.compare(number, 0) == 0 ? NumberValue.ZERO : new FloatValue(number);
            }
            case Double number -> {
                return Double.compare(number, 0) == 0 ? NumberValue.ZERO : new DoubleValue(number);
            }
            case Byte number -> {
                return number == 0 ? NumberValue.ZERO : new ByteValue(number);
            }
            case Short number -> {
                return number == 0 ? NumberValue.ZERO : new ShortValue(number);
            }
            case AtomicInteger number -> {
                int v = number.get();
                return v == 0 ? NumberValue.ZERO : new IntValue(v);
            }
            case AtomicLong number -> {
                long v = number.get();
                return v == 0 ? NumberValue.ZERO : new LongValue(v);
            }
            case null -> {
                return NumberValue.ZERO;
            }
            default -> throw new IllegalArgumentException("Unsupported Number: " + value.getClass());
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
