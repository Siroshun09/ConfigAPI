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

package com.github.siroshun09.configapi.yaml;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import com.github.siroshun09.configapi.api.file.AbstractFileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class that implements {@link Configuration} for Yaml file.
 * <p>
 * This class can load from or save to Yaml file.
 * <p>
 * In the internal, this manages keys and values in {@link MappedConfiguration}.
 */
public class YamlConfiguration extends AbstractFileConfiguration {

    /**
     * Creates the new {@link YamlConfiguration} from specified {@link Path}.
     *
     * @param path the path to load or save
     * @return the new {@link YamlConfiguration}
     */
    @Contract("_ -> new")
    public static @NotNull YamlConfiguration create(@NotNull Path path) {
        return new YamlConfiguration(path, Yaml::new);
    }

    /**
     * Creates the new {@link YamlConfiguration} from specified {@link Path} and other {@link Configuration}.
     *
     * @param path  the path to load or save
     * @param other the other {@link Configuration}
     * @return the new {@link YamlConfiguration}
     */
    @Contract("_, _ -> new")
    public static @NotNull YamlConfiguration create(@NotNull Path path, @NotNull Configuration other) {
        return new YamlConfiguration(path, Yaml::new, other);
    }

    /**
     * Creates the new {@link YamlConfiguration} from specified {@link Path}
     * and specified supplier of {@link Yaml}.
     *
     * @param path         the path to load or save
     * @param yamlSupplier the supplier of {@link Yaml}
     * @return the new {@link YamlConfiguration}
     */
    @Contract("_, _ -> new")
    public static @NotNull YamlConfiguration create(@NotNull Path path, @NotNull Supplier<Yaml> yamlSupplier) {
        return new YamlConfiguration(path, yamlSupplier);
    }

    /**
     * Creates the new {@link YamlConfiguration} from specified {@link Path},
     * other {@link Configuration}, and specified supplier of {@link Yaml}.
     *
     * @param path         the path to load or save
     * @param yamlSupplier the supplier of {@link Yaml}
     * @param other        the other {@link Configuration}
     * @return the new {@link YamlConfiguration}
     */
    @Contract("_, _, _ -> new")
    public static @NotNull YamlConfiguration create(@NotNull Path path, @NotNull Supplier<Yaml> yamlSupplier, @NotNull Configuration other) {
        return new YamlConfiguration(path, yamlSupplier, other);
    }

    private final ThreadLocal<Yaml> yamlThreadLocal;
    private Configuration config;

    private YamlConfiguration(@NotNull Path path, @NotNull Supplier<Yaml> yamlSupplier) {
        super(path);
        this.yamlThreadLocal = ThreadLocal.withInitial(yamlSupplier);
    }

    private YamlConfiguration(@NotNull Path path, @NotNull Supplier<Yaml> yamlSupplier, @NotNull Configuration other) {
        super(path);
        this.yamlThreadLocal = ThreadLocal.withInitial(yamlSupplier);
        this.config = MappedConfiguration.create(other);
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return config != null ? config.get(path) : null;
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        if (config == null) {
            config = MappedConfiguration.create();
        }

        config.set(path, value);
    }

    @Override
    public @NotNull @Unmodifiable Set<String> getPaths() {
        return config != null ? config.getPaths() : Collections.emptySet();
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> getValues() {
        return config != null ? config.getValues() : Collections.emptySet();
    }

    @Override
    public @Nullable Configuration getSection(@NotNull String path) {
        return config != null ? config.getSection(path) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() throws IOException {
        config = null;

        if (!Files.isRegularFile(getPath())) {
            return;
        }

        Yaml yaml = yamlThreadLocal.get();
        Map<Object, Object> map;

        try (var reader = Files.newBufferedReader(getPath())) {
            map = yaml.loadAs(reader, LinkedHashMap.class);
        }

        config = MappedConfiguration.create(map);

        setLoaded(true);
    }

    @Override
    public void save() throws IOException {
        Yaml yaml = yamlThreadLocal.get();

        var map = new LinkedHashMap<>();

        if (config != null) {
            for (var key : config.getPaths()) {
                map.put(key, config.get(key));
            }
        }

        try (var writer = Files.newBufferedWriter(getPath())) {
            yaml.dump(map, writer);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YamlConfiguration that = (YamlConfiguration) o;
        return yamlThreadLocal.equals(that.yamlThreadLocal) && Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yamlThreadLocal, config);
    }

    @Override
    public String toString() {
        return "YamlConfiguration{" +
                "yamlThreadLocal=" + yamlThreadLocal +
                ", config=" + config +
                '}';
    }
}
