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

package com.github.siroshun09.configapi.api.file;

import com.github.siroshun09.configapi.api.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A implementation class that gets value from {@link Properties}.
 */
public class PropertiesFileConfiguration extends AbstractFileConfiguration {

    /**
     * Creates a new {@link PropertiesFileConfiguration}.
     *
     * @param path the filepath to load
     * @return the new {@link PropertiesFileConfiguration}
     */
    public static @NotNull PropertiesFileConfiguration create(@NotNull Path path) {
        return create(path, new Properties());
    }

    /**
     * Creates a new {@link PropertiesFileConfiguration}.
     *
     * @param path       the filepath to load
     * @param properties the {@link Properties}
     * @return the new {@link PropertiesFileConfiguration}
     */
    public static @NotNull PropertiesFileConfiguration create(@NotNull Path path, @NotNull Properties properties) {
        return new PropertiesFileConfiguration(path, properties);
    }

    private final Properties properties;

    private PropertiesFileConfiguration(@NotNull Path path, @NotNull Properties properties) {
        super(path);
        this.properties = properties;
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return properties.get(path);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        boolean remove = value == null;

        if (remove) {
            properties.remove(path);
        } else {
            properties.setProperty(path, value instanceof String ? (String) value : value.toString());
        }
    }

    @Override
    public @NotNull @Unmodifiable Set<String> getPaths() {
        return properties.keySet()
                .stream()
                .map(object -> object instanceof String ? (String) object : object.toString())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> getValues() {
        return properties.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @param path the path to get the {@link Configuration} section
     * @return {@code null}
     * @deprecated This method always returns {@code null}.
     */
    @Override
    @Deprecated
    public @Nullable Configuration getSection(@NotNull String path) {
        return null;
    }

    @Override
    public void load() throws IOException {
        properties.clear();

        if (!Files.isRegularFile(getPath())) {
            return;
        }

        try (var reader = Files.newBufferedReader(getPath())) {
            properties.load(reader);
        }

        setLoaded(true);
    }

    @Override
    public void save() throws IOException {
        save(null);
    }

    /**
     * Saves {@link Properties} with comments.
     *
     * @param comments the comments or {@code null}
     * @throws IOException if an I/O error occurs
     */
    public void save(@Nullable String comments) throws IOException {
        try (var writer = Files.newBufferedWriter(getPath())) {
            properties.store(writer, comments);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertiesFileConfiguration that = (PropertiesFileConfiguration) o;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "PropertiesFileConfiguration{" +
                "properties=" + properties +
                '}';
    }
}
