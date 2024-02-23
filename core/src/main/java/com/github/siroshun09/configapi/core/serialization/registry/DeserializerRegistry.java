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

package com.github.siroshun09.configapi.core.serialization.registry;

import com.github.siroshun09.configapi.core.serialization.Deserializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A registry that manages {@link Deserializer}s.
 * <p>
 * The implementations of this interface are thread-safe.
 *
 * @param <S> a type of objects to deserialize
 */
public sealed interface DeserializerRegistry<S> permits DeserializerRegistryImpl, DeserializerRegistryImpl.EmptyDeserializerRegistry, SerializationRegistryImpl.ReferenceDeserializerRegistry {

    /**
     * Creates a new {@link DeserializerRegistry}.
     *
     * @param <S> a type of objects to deserialize
     * @return a new {@link DeserializerRegistry}
     */
    @Contract(" -> new")
    static <S> @NotNull DeserializerRegistry<S> create() {
        return new DeserializerRegistryImpl<>();
    }

    /**
     * Returns an empty {@link DeserializerRegistry}.
     * <p>
     * The returning {@link DeserializerRegistry} is frozen.
     *
     * @param <S> a type of objects to deserialize
     * @return an empty {@link DeserializerRegistry}
     */
    @SuppressWarnings("unchecked")
    static <S> @NotNull DeserializerRegistry<S> empty() {
        return (DeserializerRegistry<S>) DeserializerRegistryImpl.EMPTY;
    }

    /**
     * Gets the {@link Deserializer} associated with the specified class.
     *
     * @param clazz the class to get {@link Deserializer}
     * @param <T>   a type of object after deserialization
     * @return the {@link Deserializer} associated with the specified class, or {@code null}
     */
    <T> @Nullable Deserializer<S, T> get(@NotNull Class<T> clazz);

    /**
     * Gets the {@link Deserializer} associated with the specified class.
     *
     * @param clazz the class to get {@link Deserializer}
     * @param <T>   a type of object after deserialization
     * @return the {@link Deserializer} associated with the specified class, or {@link Optional#empty()}.
     */
    default <T> @NotNull Optional<Deserializer<S, T>> getAsOptional(@NotNull Class<T> clazz) {
        return Optional.ofNullable(this.get(clazz));
    }

    /**
     * Registers {@link Deserializer}.
     *
     * @param clazz        a class to associate {@link Deserializer} with
     * @param deserializer a {@link Deserializer} to register
     * @param <T>          a type of object after deserialization
     * @return this {@link DeserializerRegistry} instance
     */
    @Contract("_, _ -> this")
    <T> @NotNull DeserializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Deserializer<? super S, ? extends T> deserializer);

    /**
     * Registers {@link Deserializer}s in the given {@link DeserializerRegistry}.
     *
     * @param registry a {@link DeserializerRegistry} that contains {@link Deserializer}s to register
     * @return this {@link DeserializerRegistry} instance
     */
    @Contract("_ -> this")
    @NotNull DeserializerRegistry<S> registerAll(@NotNull DeserializerRegistry<S> registry);

    /**
     * Freezes this {@link DeserializerRegistry}.
     * <p>
     * After calling this method, {@link #register(Class, Deserializer)} and {@link #registerAll(DeserializerRegistry)} will throw {@link IllegalStateException}.
     * <p>
     * This method can be called multiple times.
     *
     * @return this {@link DeserializerRegistry} instance
     */
    @Contract("-> this")
    @NotNull DeserializerRegistry<S> freeze();

    /**
     * Checks if this {@link DeserializerRegistry} is frozen.
     *
     * @return {@code true} if this {@link DeserializerRegistry} is frozen, otherwise {@code false}
     */
    boolean isFrozen();

}
