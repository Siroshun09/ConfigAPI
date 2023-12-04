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

/**
 * A {@link Node} implementation that holds a double value.
 *
 * @param doubleValue a double value
 */
public record DoubleValue(double doubleValue) implements NumberValue, StringRepresentable {

    @Override
    public @NotNull Double value() {
        return this.doubleValue;
    }

    @Override
    public int asInt() {
        return (int) this.doubleValue;
    }

    @Override
    public long asLong() {
        return (long) this.doubleValue;
    }

    @Override
    public float asFloat() {
        return (float) this.doubleValue;
    }

    @Override
    public double asDouble() {
        return this.doubleValue;
    }

    @Override
    public byte asByte() {
        return (byte) this.doubleValue;
    }

    @Override
    public short asShort() {
        return (short) this.doubleValue;
    }

    @Override
    public @NotNull String asString() {
        return Double.toString(this.doubleValue);
    }
}
