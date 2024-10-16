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
 * A {@link Node} implementation that holds a short value.
 *
 * @param shortValue a short value
 */
public record ShortValue(short shortValue) implements NumberValue, StringRepresentable {

    @Override
    public @NotNull Short value() {
        return this.shortValue;
    }

    @Override
    public int asInt() {
        return this.shortValue;
    }

    @Override
    public long asLong() {
        return this.shortValue;
    }

    @Override
    public float asFloat() {
        return this.shortValue;
    }

    @Override
    public double asDouble() {
        return this.shortValue;
    }

    @Override
    public byte asByte() {
        return (byte) this.shortValue;
    }

    @Override
    public short asShort() {
        return this.shortValue;
    }

    @Override
    public int compareTo(@NotNull NumberValue o) {
        return Short.compare(this.shortValue, o.asShort());
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public @NotNull String asString() {
        return Short.toString(this.shortValue);
    }
}
