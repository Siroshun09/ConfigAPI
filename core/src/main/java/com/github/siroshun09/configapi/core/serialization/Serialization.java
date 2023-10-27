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

package com.github.siroshun09.configapi.core.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An interface that holds {@link Serializer} and/or {@link Deserializer}.
 *
 * @param <T> the type of objects
 * @param <S> the type of serialized results
 */
public interface Serialization<T, S> {

    /**
     * Creates a new {@link Serialization} from {@link Serializer} and {@link Deserializer}.
     *
     * @param serializer   the {@link Serializer}
     * @param deserializer the {@link Deserializer}
     * @param <T>          the type of objects
     * @param <S>          the type of serialized results
     * @return the {@link Serialization} that has {@link Serializer} and {@link Deserializer}
     */
    @SuppressWarnings("unchecked")
    static <T, S> @NotNull Serialization<T, S> create(@NotNull Serializer<? super T, ? extends S> serializer,
                                                      @NotNull Deserializer<? super S, ? extends T> deserializer) {
        Objects.requireNonNull(serializer);
        Objects.requireNonNull(deserializer);
        return new DelegatingSerialization<>((Serializer<T, S>) serializer, (Deserializer<S, T>) deserializer);
    }

    /**
     * Creates a new {@link Serialization} from {@link Serializer}.
     *
     * @param serializer the {@link Serializer}
     * @param <T>        the type of objects
     * @param <S>        the type of serialized results
     * @return the {@link Serialization} that has only {@link Serializer}
     */
    @SuppressWarnings("unchecked")
    static <T, S> @NotNull Serialization<T, S> onlySerializer(@NotNull Serializer<? super T, ? extends S> serializer) {
        Objects.requireNonNull(serializer);
        return new DelegatingSerialization<>((Serializer<T, S>) serializer, null);
    }

    /**
     * Creates a new {@link Serialization} from {@link Deserializer}.
     *
     * @param deserializer the {@link Deserializer}
     * @param <T>          the type of objects
     * @param <S>          the type of serialized results
     * @return the {@link Serialization} that has only {@link Deserializer}
     */
    @SuppressWarnings("unchecked")
    static <T, S> @NotNull Serialization<T, S> onlyDeserializer(@NotNull Deserializer<? super S, ? extends T> deserializer) {
        Objects.requireNonNull(deserializer);
        return new DelegatingSerialization<>(null, (Deserializer<S, T>) deserializer);
    }

    /**
     * Checks if this {@link Serialization} has {@link Serializer}.
     *
     * @return {@code true} if this {@link Serialization} has {@link Serializer}, otherwise {@code false}
     */
    boolean hasSerializer();

    /**
     * Gets {@link Serializer} which this {@link Serialization} has.
     *
     * @return {@link Serializer} which this {@link Serialization} has
     */
    @NotNull Serializer<T, S> serializer();

    /**
     * Checks if this {@link Serialization} has {@link Deserializer}.
     *
     * @return {@code true} if this {@link Serialization} has {@link Deserializer}, otherwise {@code false}
     */
    boolean hasDeserializer();

    /**
     * Gets {@link Deserializer} which this {@link Serialization} has.
     *
     * @return {@link Deserializer} which this {@link Serialization} has
     */
    @NotNull Deserializer<S, T> deserializer();
}
