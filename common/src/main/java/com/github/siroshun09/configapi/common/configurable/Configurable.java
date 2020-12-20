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

package com.github.siroshun09.configapi.common.configurable;

import com.github.siroshun09.configapi.common.Configuration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * An interface that represents configurable value and their configuration path.
 *
 * @param <T> The value type.
 */
public interface Configurable<T> {

    /**
     * Creates {@link BooleanValue}.
     *
     * @param path The path
     * @param def The default boolean value
     * @return {@link BooleanValue}
     */
    static @NotNull BooleanValue create(@NotNull String path, boolean def) {
        return new BooleanValue(path, def);
    }

    /**
     * Creates {@link DoubleValue}.
     *
     * @param path The path
     * @param def The default double value
     * @return {@link DoubleValue}
     */
    @Contract("_, _ -> new")
    static @NotNull DoubleValue create(@NotNull String path, double def) {
        return new DoubleValue(path, def);
    }

    /**
     * Creates {@link FloatValue}.
     *
     * @param path The path
     * @param def The default float value
     * @return {@link FloatValue}
     */
    @Contract("_, _ -> new")
    static @NotNull FloatValue create(@NotNull String path, float def) {
        return new FloatValue(path, def);
    }

    /**
     * Creates {@link IntegerValue}.
     *
     * @param path The path
     * @param def The default integer value
     * @return {@link IntegerValue}
     */
    @Contract("_, _ -> new")
    static @NotNull IntegerValue create(@NotNull String path, int def) {
        return new IntegerValue(path, def);
    }

    /**
     * Creates {@link LongValue}.
     *
     * @param path The path
     * @param def The default long value
     * @return {@link LongValue}
     */
    @Contract("_, _ -> new")
    static @NotNull LongValue create(@NotNull String path, long def) {
        return new LongValue(path, def);
    }

    /**
     * Creates {@link StringValue}.
     *
     * @param path The path
     * @param def The default string value
     * @return {@link StringValue}
     */
    @Contract("_, _ -> new")
    static @NotNull StringValue create(@NotNull String path, @NotNull String def) {
        return new StringValue(path, def);
    }

    /**
     * Creates {@link DoubleList}.
     *
     * @param path The path
     * @param def The default double list
     * @return {@link DoubleList}
     */
    @Contract("_, _ -> new")
    static @NotNull DoubleList createDoubleList(@NotNull String path, @NotNull List<Double> def) {
        return new DoubleList(path, def);
    }

    /**
     * Creates {@link FloatList}.
     *
     * @param path The path
     * @param def The default float list
     * @return {@link FloatList}
     */
    @Contract("_, _ -> new")
    static @NotNull FloatList createFloatList(@NotNull String path, @NotNull List<Float> def) {
        return new FloatList(path, def);
    }

    /**
     * Creates {@link IntegerList}.
     *
     * @param path The path
     * @param def The default integer list
     * @return {@link IntegerList}
     */
    @Contract("_, _ -> new")
    static @NotNull IntegerList createIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        return new IntegerList(path, def);
    }

     /**
     * Creates {@link LongList}.
     *
     * @param path The path
     * @param def The default long list
     * @return {@link LongList}
     */
    @Contract("_, _ -> new")
    static @NotNull LongList createLongList(@NotNull String path, @NotNull List<Long> def) {
        return new LongList(path, def);
    }

    /**
     * Creates {@link StringList}.
     *
     * @param path The path
     * @param def The default string list
     * @return {@link StringList}
     */
    @Contract("_, _ -> new")
    static @NotNull StringList createStringList(@NotNull String path, @NotNull List<String> def) {
        return new StringList(path, def);
    }

    /**
     * Gets the key to get the value from {@link Configuration}.
     *
     * @return The key
     */
    @NotNull String getKey();

    /**
     * Gets the default value of this configurable value.
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
     * @param configuration  The configuration.
     * @return The requested value, or {@code null} if could not get.
     */
    @Nullable T getValueOrNull(@NotNull Configuration configuration);

    /**
     * Serialize the value.
     *
     * The default implementation of this method returns the arguments as is.
     *
     * This method will be used when {@link Configuration#setValue(Configurable, Object)} is called.
     * 
     * @param value The value to serialize.
     * @return The serialized value or the arguments as is.
     */
    default @NotNull Object serialize(@NotNull T value) {
        return value;
    }
}
