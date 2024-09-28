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

package dev.siroshun.configapi.core.node;

import dev.siroshun.configapi.core.node.visitor.NodeVisitor;
import dev.siroshun.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Node} implementation that holds a long value.
 *
 * @param longValue a long value
 */
public record LongValue(long longValue) implements NumberValue, StringRepresentable {

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

    @Override
    public int compareTo(@NotNull NumberValue o) {
        return Long.compare(this.longValue, o.asLong());
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public @NotNull String asString() {
        return Long.toString(this.longValue);
    }
}
