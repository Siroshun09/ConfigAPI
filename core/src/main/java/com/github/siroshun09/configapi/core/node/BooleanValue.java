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

import com.github.siroshun09.configapi.core.node.visitor.NodeVisitor;
import com.github.siroshun09.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Node} implementation that holds a boolean value.
 *
 * @param booleanValue a boolean
 */
public record BooleanValue(boolean booleanValue) implements ValueNode<Boolean>, StringRepresentable {

    /**
     * An instance of {@link BooleanValue} that indicates {@code true}.
     */
    public static final BooleanValue TRUE = new BooleanValue(true);

    /**
     * An instance of {@link BooleanValue} that indicates {@code false}.
     */
    public static final BooleanValue FALSE = new BooleanValue(false);

    /**
     * Creates a {@link BooleanValue} from the given {@link Boolean}.
     *
     * @param bool a {@link Boolean} to create {@link BooleanValue}
     * @return {@link #TRUE} if the given {@link Boolean} equals {@link Boolean#TRUE}, otherwise {@link #FALSE}
     */
    public static @NotNull BooleanValue fromBoolean(@Nullable Boolean bool) {
        return Boolean.TRUE.equals(bool) ? TRUE : FALSE;
    }

    /**
     * Creates a {@link BooleanValue} from the given boolean.
     *
     * @param bool a boolean to create {@link BooleanValue}
     * @return {@link #TRUE} if the given boolean is {@code true}, otherwise {@link #FALSE}
     */
    public static @NotNull BooleanValue fromBoolean(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    /**
     * A constructor of {@link BooleanValue}.
     * <p>
     * This constructor is marked as {@link org.jetbrains.annotations.ApiStatus.Internal}.
     * Please use constants ({@link #TRUE} or {@link #FALSE}) or static methods ({@link #fromBoolean(Boolean)} or {@link #fromBoolean(boolean)}).
     *
     * @param booleanValue a boolean to create {@link BooleanValue}
     */
    @ApiStatus.Internal
    public BooleanValue {
    }

    @Override
    public @NotNull Boolean value() {
        return this.booleanValue;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    /**
     * Gets the value as a primitive boolean.
     * <p>
     * This method returns the same value as {@link #booleanValue()}.
     *
     * @return a primitive boolean
     */
    public boolean asBoolean() {
        return this.booleanValue;
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public @NotNull String asString() {
        return Boolean.toString(this.booleanValue);
    }
}
