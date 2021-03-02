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

package com.github.siroshun09.configapi.common;

import com.github.siroshun09.configapi.common.serialize.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractConfiguration implements Configuration {

    protected static final String KEY_SEPARATOR_STRING = String.valueOf(KEY_SEPARATOR);
    protected static final String KEY_SEPARATOR_REGEX = "\\" + KEY_SEPARATOR;

    private final Map<String, Object> map;

    protected AbstractConfiguration() {
        map = new LinkedHashMap<>();
    }

    protected AbstractConfiguration(@NotNull Map<String, Object> map) {
        this.map = Objects.requireNonNull(map);
    }

    protected AbstractConfiguration(@NotNull AbstractConfiguration other) {
        this.map = other.getMap();
    }

    protected @NotNull Map<String, Object> getMap() {
        return map;
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        Objects.requireNonNull(path);

        if (!path.contains(KEY_SEPARATOR_STRING)) {
            return map.get(path);
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        int lastIndex = keys.length - 1;

        AbstractConfiguration parent = getParentOfPath(keys, lastIndex);

        return parent != null ? parent.map.get(keys[lastIndex]) : null;
    }

    @Override
    public @NotNull Collection<String> getKeys() {
        return map.keySet();
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path);

        boolean remove = value == null;

        if (!path.contains(KEY_SEPARATOR_STRING)) {
            if (remove) {
                map.remove(path);
            } else {
                putValue(path, value);
            }
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        int lastIndex = keys.length - 1;

        if (remove) {
            AbstractConfiguration parent = getParentOfPath(keys, lastIndex);

            if (parent != null) {
                parent.map.remove(keys[lastIndex]);
            }
        } else {
            getOrCreateParentOfPath(keys, lastIndex).putValue(keys[lastIndex], value);
        }
    }

    @Override
    public <T> void set(@NotNull String path, @NotNull T value, @NotNull Serializer<T> serializer) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(value);
        Objects.requireNonNull(serializer);

        Configuration serialized = serializer.serialize(value);

        if (serialized instanceof AbstractConfiguration) {
            set(path, serialized);
        }
    }

    @Override
    public @Nullable Configuration getSection(@NotNull String path) {
        Objects.requireNonNull(path);

        if (!path.contains(KEY_SEPARATOR_STRING)) {
            return getDirectChild(path);
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        AbstractConfiguration current = this;

        for (String key : keys) {
            current = current.getDirectChild(key);

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    @Override
    public String toString() {
        return "ConfigurationImpl{" +
                "map=" + map +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractConfiguration)) return false;
        AbstractConfiguration that = (AbstractConfiguration) o;
        return getMap().equals(that.getMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMap());
    }

    private void putValue(@NotNull String key, @NotNull Object object) {
        if (object instanceof AbstractConfiguration) {
            map.put(checkKey(key), ((AbstractConfiguration) object).map);
        } else {
            map.put(checkKey(key), object);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable AbstractConfiguration getDirectChild(@NotNull String key) {
        Object object = get(key);

        if (object instanceof Map) {
            return new ConfigurationImpl((Map<String, Object>) object);
        } else {
            return null;
        }
    }

    private @Nullable AbstractConfiguration getParentOfPath(@NotNull String[] keys, int lastIndex) {
        AbstractConfiguration current = this;

        for (int i = 0; i < lastIndex && current != null; i++) {
            current = current.getDirectChild(keys[i]);
        }

        return current;
    }

    private @NotNull AbstractConfiguration getOrCreateParentOfPath(@NotNull String[] keys, int lastIndex) {
        AbstractConfiguration parent = this;
        AbstractConfiguration current;

        for (int i = 0; i < lastIndex; i++) {
            current = parent.getDirectChild(keys[i]);

            if (current == null) {
                Map<String, Object> childMap = new LinkedHashMap<>();
                parent.map.put(checkKey(keys[i]), childMap);
                current = new ConfigurationImpl(childMap);
            }

            parent = current;
        }

        return parent;
    }

    private @NotNull String checkKey(String key) {
        if (key == null || key.isEmpty() || key.contains(KEY_SEPARATOR_STRING)) {
            throw new IllegalArgumentException(key + " is invalid key.");
        }

        return key;
    }
}
