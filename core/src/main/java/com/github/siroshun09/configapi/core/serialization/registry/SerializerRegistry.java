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

import com.github.siroshun09.configapi.core.serialization.Serializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A registry that manages {@link Serializer}s.
 * <p>
 * The implementations of this interface are thread-safe.
 *
 * @param <S> a type of objects after serializing
 */
public sealed interface SerializerRegistry<S> permits SerializationRegistryImpl.ReferenceSerializerRegistry, SerializerRegistryImpl, SerializerRegistryImpl.EmptySerializerRegistry {

    /**
     * Creates a new {@link SerializerRegistry}.
     *
     * @param <S> a type of objects after serializing
     * @return a new {@link SerializerRegistry}
     */
    @Contract(" -> new")
    static <S> @NotNull SerializerRegistry<S> create() {
        return new SerializerRegistryImpl<>();
    }

    /**
     * Returns an empty {@link SerializerRegistry}.
     * <p>
     * The returning {@link SerializerRegistry} is frozen.
     *
     * @param <S> a type of objects after serializing
     * @return an empty {@link SerializerRegistry}
     */
    @SuppressWarnings("unchecked")
    static <S> @NotNull SerializerRegistry<S> empty() {
        return SerializerRegistryImpl.EMPTY;
    }

    /**
     * Gets the {@link Serializer} associated with the specified class.
     *
     * @param clazz the class to get {@link Serializer}
     * @param <T>   a type of object to serialize
     * @return the {@link Serializer} associated with the specified class, or {@code null}
     */
    <T> @Nullable Serializer<T, S> get(@NotNull Class<T> clazz);

    /**
     * Gets the {@link Serializer} associated with the specified class.
     *
     * @param clazz the class to get {@link Serializer}
     * @param <T>   a type of object to serialize
     * @return the {@link Serializer} associated with the specified class, or {@link Optional#empty()}
     */
    default <T> @NotNull Optional<Serializer<T, S>> getAsOptional(@NotNull Class<T> clazz) {
        return Optional.ofNullable(this.get(clazz));
    }

    /**
     * Registers {@link Serializer}.
     *
     * @param clazz      a class to associate {@link Serializer} with
     * @param serializer a {@link Serializer} to register
     * @param <T>        a type of object to serialize
     * @return this {@link SerializerRegistry} instance
     */
    @Contract("_, _ -> this")
    <T> @NotNull SerializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Serializer<? super T, ? extends S> serializer);

    /**
     * Registers {@link Serializer}s in the given {@link SerializerRegistry}.
     *
     * @param registry a {@link SerializerRegistry} that contains {@link Serializer}s to register
     * @return this {@link SerializerRegistry} instance
     */
    @Contract("_ -> this")
    @NotNull SerializerRegistry<S> registerAll(@NotNull SerializerRegistry<S> registry);

    /**
     * Freezes this {@link SerializerRegistry}.
     * <p>
     * After calling this method, {@link #register(Class, Serializer)} and {@link #registerAll(SerializerRegistry)} will throw {@link IllegalStateException}.
     * <p>
     * This method can be called multiple times.
     *
     * @return this {@link SerializerRegistry} instance
     */
    @Contract("-> this")
    @NotNull SerializerRegistry<S> freeze();

    /**
     * Checks if this {@link SerializerRegistry} is frozen.
     *
     * @return {@code true} if this {@link SerializerRegistry} is frozen, otherwise {@code false}
     */
    boolean isFrozen();

}
