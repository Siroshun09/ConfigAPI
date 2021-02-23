/*
 *     Copyright 2020 Siroshun09
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

package com.github.siroshun09.configapi.common.defaultvalue;

import com.github.siroshun09.configapi.common.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An interface that represents default value and their configuration path.
 *
 * @param <T> The value type.
 */
public interface DefaultValue<T> {

    /**
     * Gets the key to get the value from {@link Configuration}.
     *
     * @return The key
     */
    @NotNull String getKey();

    /**
     * Gets the default value.
     *
     * @return The default value
     */
    @NotNull T getDefault();

    /**
     * Gets the value from {@link Configuration}.
     *
     * @param configuration The configuration
     * @return The requested value
     */
    default @NotNull T getValue(@NotNull Configuration configuration) {
        T value = getValueOrNull(configuration);
        return Optional.ofNullable(value).orElse(getDefault());
    }

    /**
     * Gets the value from {@link Configuration}.
     *
     * @param configuration The configuration.
     * @return The requested value, or {@code null} if could not get.
     */
    @Nullable T getValueOrNull(@NotNull Configuration configuration);

    /**
     * Serialize the value.
     * <p>
     * The default implementation of this method returns the arguments as is.
     * <p>
     * This method will be used when {@link Configuration#setValue(DefaultValue, Object)} is called.
     *
     * @param value The value to serialize.
     * @return The serialized value or the arguments as is.
     */
    default @NotNull Object serialize(@NotNull T value) {
        return value;
    }
}
