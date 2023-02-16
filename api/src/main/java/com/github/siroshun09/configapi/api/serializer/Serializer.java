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

package com.github.siroshun09.configapi.api.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface that represents serializer.
 *
 * @param <T> the type of value to serialize
 * @param <S> the type after serialization
 */
public interface Serializer<T, S> {

    /**
     * Serializes the value.
     *
     * @param input the value to serialize
     * @return the serialized value
     */
    @NotNull S serialize(@NotNull T input);

    /**
     * Deserializes the value.
     *
     * @param source the value to deserialize
     * @return the deserialized value or {@code null} if could not deserialize
     */
    @Nullable T deserialize(@NotNull Object source);
}
