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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationImpl implements Configuration {

    protected static final String KEY_SEPARATOR_STRING = String.valueOf(KEY_SEPARATOR);
    protected static final String KEY_SEPARATOR_REGEX = "\\" + KEY_SEPARATOR;

    @Contract(value = " -> new", pure = true)
    static @NotNull Configuration createEmpty() {
        return new ConfigurationImpl();
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Configuration createNew(@NotNull Map<String, Object> map) {
        return new ConfigurationImpl(map);
    }

    private final Map<String, Object> map;

    protected ConfigurationImpl() {
        map = new LinkedHashMap<>();
    }

    protected ConfigurationImpl(@NotNull Map<String, Object> map) {
        this.map = map;
    }

    protected @NotNull Map<String, Object> getMap() {
        return map;
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        if (!path.contains(KEY_SEPARATOR_STRING)) {
            return map.get(path);
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        int lastIndex = keys.length - 1;

        ConfigurationImpl parent = getParentOfPath(keys, lastIndex);

        return parent != null ? parent.map.get(keys[lastIndex]) : null;
    }

    @Override
    public @NotNull Collection<String> getKeys() {
        return map.keySet();
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        boolean remove = value == null;

        if (!path.contains(KEY_SEPARATOR_STRING)) {
            if (remove) {
                map.remove(path);
            } else {
                map.put(path, value);
            }
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        int lastIndex = keys.length - 1;

        if (remove) {
            ConfigurationImpl parent = getParentOfPath(keys, lastIndex);

            if (parent != null) {
                parent.map.remove(keys[lastIndex]);
            }

            return;
        }

        getParentOfPathOrCreate(keys, lastIndex).map.put(keys[lastIndex], value);
    }

    @Override
    public @Nullable Configuration getSection(@NotNull String path) {
        if (!path.contains(KEY_SEPARATOR_STRING)) {
            return getDirectChild(path);
        }

        String[] keys = path.split(KEY_SEPARATOR_REGEX);
        ConfigurationImpl current = this;

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

    @SuppressWarnings("unchecked")
    private @Nullable ConfigurationImpl getDirectChild(@NotNull String key) {
        Object object = get(key);

        if (object instanceof Map) {
            return new ConfigurationImpl((Map<String, Object>) object);
        } else {
            return null;
        }
    }

    private @Nullable ConfigurationImpl getParentOfPath(@NotNull String[] keys, int lastIndex) {
        ConfigurationImpl current = this;

        for (int i = 0; i < lastIndex && current != null; i++) {
            current = current.getDirectChild(keys[i]);
        }

        return current;
    }

    private @NotNull ConfigurationImpl getParentOfPathOrCreate(@NotNull String[] keys, int lastIndex) {
        ConfigurationImpl parent = this;
        ConfigurationImpl current;

        for (int i = 0; i < lastIndex; i++) {
            current = parent.getDirectChild(keys[i]);

            if (current == null) {
                Map<String, Object> childMap = new LinkedHashMap<>();
                parent.map.put(keys[i], childMap);
                current = new ConfigurationImpl(childMap);
            }

            parent = current;
        }

        return parent;
    }
}
