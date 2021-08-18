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

package com.github.siroshun09.configapi.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class that implements {@link Configuration}.
 * <p>
 * This class manages keys and values as a map.
 */
public class MappedConfiguration extends AbstractConfiguration {

    /**
     * Creates the new {@link Configuration}.
     *
     * @return the new {@link Configuration}
     */
    @Contract(" -> new")
    public static @NotNull Configuration create() {
        return new MappedConfiguration(new LinkedHashMap<>());
    }

    /**
     * Creates the new {@link Configuration} by copying the keys and values from another {@link Configuration}.
     *
     * @param other the source {@link Configuration}
     * @return the new {@link Configuration}
     */
    @Contract("_ -> new")
    public static @NotNull Configuration create(@NotNull Configuration other) {
        var map = convertToMap(other);
        return new MappedConfiguration(map);
    }

    /**
     * Creates the new {@link Configuration} from the map.
     *
     * @param map the map to create {@link Configuration}
     * @return the new {@link Configuration}
     */
    public static @NotNull Configuration create(@NotNull Map<Object, Object> map) {
        return new MappedConfiguration(new LinkedHashMap<>(map));
    }

    private static @NotNull Map<Object, Object> convertToMap(@NotNull Configuration config) {
        if (config instanceof MappedConfiguration) {
            return ((MappedConfiguration) config).map;
        }

        var map = new LinkedHashMap<>();

        for (var key : config.getKeyList()) {
            var value = config.get(key);

            if (value instanceof Configuration) {
                value = convertToMap((Configuration) value);
            }

            map.put(key, value);
        }

        return map;
    }

    private final Map<Object, Object> map;

    private MappedConfiguration(@NotNull Map<Object, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object get(@NotNull String path) {
        checkPath(path);

        if (!path.contains(PATH_SEPARATOR_STRING)) {
            return map.get(path);
        }

        var paths = splitPath(path);
        var lastIndex = paths.length - 1;
        var current = map;

        for (int i = 0; i < lastIndex && current != null; i++) {
            var key = checkKey(paths[i]);
            var value = current.get(key);
            current = value instanceof Map ? (Map<Object, Object>) value : null;
        }

        var key = checkKey(paths[lastIndex]);
        return current != null ? current.get(key) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        checkPath(path);
        boolean remove = value == null;

        if (value instanceof Configuration) {
            value = convertToMap((Configuration) value);
        }

        if (!path.contains(PATH_SEPARATOR_STRING)) {
            checkKey(path);

            if (remove) {
                map.remove(path);
            } else {
                map.put(path, value);
            }

            return;
        }

        var paths = splitPath(path);
        var lastIndex = paths.length - 1;
        var current = map;

        for (int i = 0; i < lastIndex && current != null; i++) {
            var key = checkKey(paths[i]);
            var object = current.get(key);

            if (object instanceof Map) {
                current = (Map<Object, Object>) object;
            } else {
                if (remove) {
                    current = null;
                } else {
                    var newMap = new LinkedHashMap<>();
                    current.put(key, newMap);
                    current = newMap;
                }
            }
        }

        if (current == null) {
            return;
        }

        var key = checkKey(paths[lastIndex]);

        if (remove) {
            current.remove(key);
        } else {
            current.put(key, value);
        }
    }

    @Override
    public @NotNull @Unmodifiable List<String> getKeyList() {
        return map.keySet().stream()
                .map(object -> object instanceof String ? (String) object : object.toString())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> getValues() {
        return map.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Configuration getSection(@NotNull String path) {
        var value = get(path);
        return value instanceof Map ? new MappedConfiguration((Map<Object, Object>) value) : null;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        var that = (MappedConfiguration) other;
        return map.equals(that.map);
    }

    @Override
    public String toString() {
        return "MappedConfiguration{" +
                "map=" + map +
                '}';
    }

    /**
     * Gets the map.
     *
     * @return the map
     */
    protected @NotNull Map<Object, Object> getMap() {
        return map;
    }

    private void checkPath(String path) {
        if (path == null || path.isEmpty() ||
                path.charAt(0) == PATH_SEPARATOR ||
                path.charAt(path.length() - 1) == PATH_SEPARATOR) {
            throw new IllegalArgumentException(path + " is invalid path.");
        }
    }

    @Contract("null -> fail")
    private @NotNull String checkKey(String key) {
        if (key == null ||
                key.isEmpty() ||
                key.contains(PATH_SEPARATOR_STRING)) {
            throw new IllegalArgumentException(key + " is invalid key.");
        }

        return key;
    }

    private @Nullable Map<?, ?> getChild(@NotNull String key) {
        var value = map.get(key);
        return value instanceof Map ? (Map<?, ?>) value : null;
    }

    private @NotNull String[] splitPath(@NotNull String path) {
        var builder = new StringBuilder();
        var result = new String[Math.toIntExact(path.chars().filter(c -> c == PATH_SEPARATOR).count() + 1)];

        var currentKeyPosition = 0;
        var lastIndex = path.length() - 1;

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);

            if (c == PATH_SEPARATOR) {
                var key = builder.toString();
                result[currentKeyPosition] = checkKey(key);

                currentKeyPosition++;
                builder.setLength(0);
            } else if (i == lastIndex) {
                var key = builder.append(c).toString();
                result[currentKeyPosition] = checkKey(key);
            } else {
                builder.append(c);
            }
        }

        return result;
    }
}
