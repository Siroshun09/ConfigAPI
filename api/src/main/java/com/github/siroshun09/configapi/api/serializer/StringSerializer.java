/*
 *     Copyright 2021 Siroshun09
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
 * An interface that converts an object to a {@link String}.
 *
 * @param <T> the type of value
 */
public interface StringSerializer<T> extends Serializer<T, String> {

    @Override
    @NotNull String serialize(@NotNull T value);

    @Override
    default @Nullable T deserialize(@NotNull Object source) {
        var string = source instanceof String ? (String) source : source.toString();
        return deserializeString(string);
    }

    /**
     * Deserializes the {@link String}.
     *
     * @param source the string to deserialize
     * @return the deserialized value or {@code null} if string could not be deserialized
     */
    @Nullable T deserializeString(@NotNull String source);
}
