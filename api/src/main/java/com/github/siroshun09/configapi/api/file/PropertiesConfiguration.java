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

package com.github.siroshun09.configapi.api.file;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A implementation class that gets value from {@link Properties}.
 */
public class PropertiesConfiguration extends AbstractFileConfiguration {

    /**
     * Creates a new {@link PropertiesConfiguration}.
     *
     * @param path the filepath to load
     * @return the new {@link PropertiesConfiguration}
     */
    public static @NotNull PropertiesConfiguration create(@NotNull Path path) {
        return create(path, new Properties());
    }

    /**
     * Creates a new {@link PropertiesConfiguration}.
     *
     * @param path       the filepath to load
     * @param properties the {@link Properties}
     * @return the new {@link PropertiesConfiguration}
     */
    public static @NotNull PropertiesConfiguration create(@NotNull Path path, @NotNull Properties properties) {
        return new PropertiesConfiguration(path, copyProperties(properties));
    }

    private static @NotNull Properties copyProperties(@NotNull Properties source) {
        var newProperties = new Properties();

        for (var key : source.keySet()) {
            newProperties.put(key, source.get(key));
        }

        return newProperties;
    }

    private final Properties properties;

    private PropertiesConfiguration(@NotNull Path path, @NotNull Properties properties) {
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
    public @NotNull @Unmodifiable List<String> getKeyList() {
        return properties.keySet()
                .stream()
                .map(object -> object instanceof String ? (String) object : object.toString())
                .collect(Collectors.toUnmodifiableList());
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

    /**
     * @param path the path to get the {@link Configuration} section
     * @return {@link MappedConfiguration#create()}
     * @deprecated {@link PropertiesConfiguration} does not support the section,
     * so this method always returns {@link MappedConfiguration#create()}.
     */
    @Override
    @Deprecated
    public @NotNull Configuration getOrCreateSection(@NotNull String path) {
        return MappedConfiguration.create();
    }

    @Override
    public void clear() {
        properties.clear();
        setLoaded(false);
    }

    @Override
    public @NotNull PropertiesConfiguration copy() {
        var copied = create(getPath(), properties);

        copied.setLoaded(isLoaded());

        return copied;
    }

    @Override
    public void load() throws IOException {
        clear();

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
        var parent = getPath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (var writer = Files.newBufferedWriter(getPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            properties.store(writer, comments);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertiesConfiguration that = (PropertiesConfiguration) o;
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
