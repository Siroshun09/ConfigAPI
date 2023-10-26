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
 * A {@link Node} implementation that holds a long value.
 *
 * @param longValue a long value
 */
public record LongValue(long longValue) implements NumberValue {

    @Override
    public @NotNull Long value() {
        return this.longValue;
    }

    @Override
    public int asInt() {
        return (int) this.longValue;
    }

    @Override
    public long asLong() {
        return this.longValue;
    }

    @Override
    public float asFloat() {
        return (float) this.longValue;
    }

    @Override
    public double asDouble() {
        return this.longValue;
    }

    @Override
    public byte asByte() {
        return (byte) this.longValue;
    }

    @Override
    public short asShort() {
        return (short) this.longValue;
    }
}
