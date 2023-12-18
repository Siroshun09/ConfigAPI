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

import com.github.siroshun09.configapi.core.node.visitor.NodeVisitor;
import com.github.siroshun09.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Node} implementation that holds a byte value.
 *
 * @param byteValue a byte value
 */
public record ByteValue(byte byteValue) implements NumberValue, StringRepresentable {

    @Override
    public @NotNull Byte value() {
        return this.byteValue;
    }

    @Override
    public int asInt() {
        return this.byteValue;
    }

    @Override
    public long asLong() {
        return this.byteValue;
    }

    @Override
    public float asFloat() {
        return this.byteValue;
    }

    @Override
    public double asDouble() {
        return this.byteValue;
    }

    @Override
    public byte asByte() {
        return this.byteValue;
    }

    @Override
    public short asShort() {
        return this.byteValue;
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public @NotNull String asString() {
        return Byte.toString(this.byteValue);
    }
}
