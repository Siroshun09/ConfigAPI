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

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * An interface that converts an object to a {@link Configuration}.
 *
 * @param <T> the type of value
 */
public interface ConfigurationSerializer<T> extends Serializer<T, Configuration> {

    @Override
    @NotNull Configuration serialize(@NotNull T value);

    @SuppressWarnings("unchecked")
    @Override
    default @Nullable T deserialize(@NotNull Object source) {
        if (source instanceof Configuration) {
            return deserializeConfiguration((Configuration) source);
        } else if (source instanceof Map) {
            var config = MappedConfiguration.create((Map<Object, Object>) source);
            return deserializeConfiguration(config);
        } else {
            return null;
        }
    }

    /**
     * Deserializes {@link Configuration}
     *
     * @param config the config to deserialize
     * @return the deserialized value or {@code null} if could not deserialize
     */
    @Nullable T deserializeConfiguration(@NotNull Configuration config);
}
