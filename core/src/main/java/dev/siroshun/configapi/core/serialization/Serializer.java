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

package dev.siroshun.configapi.core.serialization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Function;

/**
 * An interface to "serialize" objects to other types.
 *
 * @param <I> the type of input
 * @param <O> the type of output
 */
@FunctionalInterface
public interface Serializer<I, O> extends Function<I, O> {

    /**
     * Serializes the object.
     *
     * @param input the object to serialize
     * @return the serialized result
     */
    @UnknownNullability O serialize(@NotNull I input);

    /**
     * Applies the given object to {@link #serialize(Object)}.
     *
     * @param i the function argument
     * @return the deserialized result
     * @see #serialize(Object)
     */
    @Override
    default O apply(I i) {
        return serialize(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull <V> Serializer<V, O> compose(@NotNull Function<? super V, ? extends I> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default @NotNull <V> Serializer<I, V> andThen(@NotNull Function<? super O, ? extends V> after) {
        Objects.requireNonNull(after);
        return (I t) -> after.apply(apply(t));
    }
}
