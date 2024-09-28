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

package dev.siroshun.configapi.core.serialization.registry;

import dev.siroshun.configapi.core.serialization.Deserializer;
import dev.siroshun.configapi.core.serialization.Serialization;
import dev.siroshun.configapi.core.serialization.Serializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A registry that manages {@link Serialization}s.
 * <p>
 * The implementations of this interface are thread-safe.
 *
 * @param <S> a type of objects after serializing
 */
public sealed interface SerializationRegistry<S> permits SerializationRegistryImpl, SerializationRegistryImpl.EmptySerializationRegistry {

    /**
     * Creates a new {@link SerializationRegistry}.
     *
     * @param <S> a type of objects after serializing
     * @return a new {@link SerializationRegistry}
     */
    @Contract(" -> new")
    static <S> @NotNull SerializationRegistry<S> create() {
        return new SerializationRegistryImpl<>();
    }

    /**
     * Returns an empty {@link SerializationRegistry}.
     * <p>
     * The returning {@link SerializationRegistry} is frozen.
     *
     * @param <S> a type of objects after serializing
     * @return an empty {@link SerializationRegistry}
     */
    @SuppressWarnings("unchecked")
    static <S> @NotNull SerializationRegistry<S> empty() {
        return SerializationRegistryImpl.EMPTY;
    }

    /**
     * Gets the {@link Serialization} associated with the specified class.
     *
     * @param clazz the class to get {@link Serialization}
     * @param <T>   a type of objects after serializing
     * @return the {@link Serialization} associated with the specified class, or {@code null}
     */
    <T> @Nullable Serialization<T, S> get(@NotNull Class<T> clazz);

    /**
     * Gets the {@link Serialization} associated with the specified class.
     *
     * @param clazz the class to get {@link Serialization}
     * @param <T>   a type of objects after serializing
     * @return the {@link Serialization} associated with the specified class, or {@link Optional#empty()}
     */
    default <T> @NotNull Optional<Serialization<T, S>> getAsOptional(@NotNull Class<T> clazz) {
        return Optional.ofNullable(this.get(clazz));
    }

    /**
     * Registers {@link Serialization}.
     *
     * @param clazz         a class to associate {@link Serialization} with
     * @param serialization a {@link Serialization} to register
     * @param <T>           a type of objects after serializing
     * @return this {@link SerializationRegistry} instance
     */
    @Contract("_, _ -> this")
    <T> @NotNull SerializationRegistry<S> register(@NotNull Class<T> clazz, @NotNull Serialization<? super T, ? extends S> serialization);

    /**
     * Registers {@link Serialization}s in the given {@link SerializationRegistry}.
     *
     * @param registry a {@link Serialization} that contains {@link Serialization}s to register
     * @return this {@link SerializationRegistry} instance
     */
    @Contract("_ -> this")
    @NotNull SerializationRegistry<S> registerAll(@NotNull SerializationRegistry<S> registry);

    /**
     * Freezes this {@link SerializationRegistry}.
     * <p>
     * After calling this method, {@link #register(Class, Serialization)} and {@link #registerAll(SerializationRegistry)} will throw {@link IllegalStateException}.
     * <p>
     * This method can be called multiple times.
     *
     * @return this {@link SerializationRegistry} instance
     */
    @Contract("-> this")
    @NotNull SerializationRegistry<S> freeze();

    /**
     * Checks if this {@link SerializationRegistry} is frozen.
     *
     * @return {@code true} if this {@link SerializationRegistry} is frozen, otherwise {@code false}
     */
    boolean isFrozen();

    /**
     * Gets the {@link SerializerRegistry} that refers this {@link SerializationRegistry}.
     * <p>
     * When calling {@link SerializerRegistry#register(Class, Serializer)} or {@link SerializerRegistry#registerAll(SerializerRegistry)},
     * the returning {@link SerializerRegistry} will register it to this registry
     * using {@link Serialization#onlySerializer(Serializer)} or {@link Serialization#create(Serializer, Deserializer)} (if {@link Deserializer} exists)
     *
     * @return the {@link SerializerRegistry} that refers this {@link SerializationRegistry}
     */
    @NotNull SerializerRegistry<S> asSerializerRegistry();

    /**
     * Gets the {@link DeserializerRegistry} that refers this {@link SerializationRegistry}.
     * <p>
     * When calling {@link DeserializerRegistry#register(Class, Deserializer)} or {@link DeserializerRegistry#registerAll(DeserializerRegistry)},
     * the returning {@link DeserializerRegistry} will register it to this registry
     * using {@link Serialization#onlyDeserializer(Deserializer)} or {@link Serialization#create(Serializer, Deserializer)} (if {@link Serializer} exists)
     *
     * @return the {@link DeserializerRegistry} that refers this {@link SerializationRegistry}
     */
    @NotNull DeserializerRegistry<S> asDeserializerRegistry();

}
