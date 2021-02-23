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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An abstract class that implements {@link DefaultValue}
 *
 * @param <T> The value type.
 */
public abstract class AbstractDefaultValue<T> implements DefaultValue<T> {

    private final String key;
    private final T def;

    /**
     * Constructor.
     *
     * @param key The key to get the value from {@link com.github.siroshun09.configapi.common.Configuration}.
     * @param def The default value that use if the value could not be get from {@link com.github.siroshun09.configapi.common.Configuration}.
     */
    protected AbstractDefaultValue(@NotNull String key, @NotNull T def) {
        this.key = Objects.requireNonNull(key);
        this.def = Objects.requireNonNull(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull T getDefault() {
        return def;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof DefaultValue) {
            DefaultValue<?> that = (DefaultValue<?>) o;
            return key.equals(that.getKey()) && def.equals(that.getDefault());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), def);
    }

    @Override
    public String toString() {
        return "AbstractDefaultValue{" +
                "key='" + key + '\'' +
                ", def=" + def +
                '}';
    }
}
