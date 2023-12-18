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

import java.util.Objects;

/**
 * A {@link Node} implementation that holds a {@link String} value.
 *
 * @param value a {@link String} value
 */
public record StringValue(@NotNull String value) implements ValueNode<String>, StringRepresentable {

    /**
     * A {@link StringValue} that represents an empty string.
     */
    public static final StringValue EMPTY = new StringValue("");

    /**
     * Creates a {@link StringValue} from the given string or returns {@link #EMPTY} if the given string is empty.
     *
     * @param value a {@link String} value
     * @return a {@link StringValue}
     */
    public static @NotNull StringValue fromString(@NotNull String value) {
        return value.isEmpty() ? EMPTY : new StringValue(value);
    }

    /**
     * A constructor of {@link StringValue}.
     *
     * @param value a {@link String} value
     */
    public StringValue {
        Objects.requireNonNull(value);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets the value as {@link String}.
     *
     * @return the value as {@link String}
     */
    @Override
    public @NotNull String asString() {
        return this.value;
    }
}
