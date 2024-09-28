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

import java.util.Objects;

/**
 * A {@link Node} implementation that holds an {@link Enum} value.
 *
 * @param value an {@link Enum} value
 * @param <E>   a type of {@link Enum}
 */
public record EnumValue<E extends Enum<E>>(@NotNull E value) implements ValueNode<E>, StringRepresentable {

    /**
     * A constructor of {@link EnumValue}.
     *
     * @param value an {@link Enum} value
     */
    public EnumValue {
        Objects.requireNonNull(value);
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public @NotNull String asString() {
        return this.value.name();
    }
}
