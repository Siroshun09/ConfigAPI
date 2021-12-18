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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    private static final Supplier<Yaml> DEFAULT_YAML_SUPPLIER;

    static {
        var options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        var representer = new Representer();
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        DEFAULT_YAML_SUPPLIER = () -> new Yaml(representer, options);
    }

    /**
     * Creates the new {@link YamlConfiguration} from specified {@link Path}.
     *
     * @param path the path to load or save
     * @return the new {@link YamlConfiguration}
     */
    @Contract("_ -> new")
    public static @NotNull YamlConfiguration create(@NotNull Path path) {
        return new YamlConfiguration(path, ThreadLocal.withInitial(DEFAULT_YAML_SUPPLIER));
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
        return new YamlConfiguration(path, ThreadLocal.withInitial(DEFAULT_YAML_SUPPLIER), other);
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
        return new YamlConfiguration(path, ThreadLocal.withInitial(yamlSupplier));
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
        return new YamlConfiguration(path, ThreadLocal.withInitial(yamlSupplier), other);
    }

    /**
     * Loads from {@link InputStream} using {@link Yaml#loadAs(InputStream, Class)}.
     *
     * @param input the {@link InputStream} to load
     * @return the loaded {@link Configuration}
     */
    @SuppressWarnings("unchecked")
    public static @NotNull Configuration loadFromInputStream(@NotNull InputStream input) {
        var yaml = new Yaml();
        var map = yaml.loadAs(input, LinkedHashMap.class);

        return MappedConfiguration.create(map);
    }

    private final ThreadLocal<Yaml> yamlThreadLocal;
    private Configuration config;

    private YamlConfiguration(@NotNull Path path, @NotNull ThreadLocal<Yaml> yamlThreadLocal) {
        super(path);
        this.yamlThreadLocal = yamlThreadLocal;
    }

    private YamlConfiguration(@NotNull Path path, @NotNull ThreadLocal<Yaml> yamlThreadLocal, @NotNull Configuration other) {
        super(path);
        this.yamlThreadLocal = yamlThreadLocal;
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
    public @NotNull @Unmodifiable List<String> getKeyList() {
        return config != null ? config.getKeyList() : Collections.emptyList();
    }

    @Override
    public @NotNull @Unmodifiable Set<Object> getValues() {
        return config != null ? config.getValues() : Collections.emptySet();
    }

    @Override
    public @Nullable Configuration getSection(@NotNull String path) {
        return config != null ? config.getSection(path) : null;
    }

    @Override
    public @NotNull Configuration getOrCreateSection(@NotNull String path) {
        if (config == null) {
            config = MappedConfiguration.create();
        }

        return config.getOrCreateSection(path);
    }

    @Override
    public void clear() {
        if (config != null) {
            config.clear();
            config = null;
        }

        setLoaded(false);
    }

    @Override
    public @NotNull YamlConfiguration copy() {
        var copied =
                config != null ?
                        new YamlConfiguration(getPath(), yamlThreadLocal, config) :
                        new YamlConfiguration(getPath(), yamlThreadLocal);

        copied.setLoaded(isLoaded());

        return copied;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() throws IOException {
        clear();

        if (!Files.isRegularFile(getPath())) {
            return;
        }

        Yaml yaml = yamlThreadLocal.get();
        Map<Object, Object> map;

        try (var reader = Files.newBufferedReader(getPath(), StandardCharsets.UTF_8)) {
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
            for (var key : config.getKeyList()) {
                map.put(key, config.get(key));
            }
        }

        var parent = getPath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

        try (var writer = Files.newBufferedWriter(getPath(), StandardCharsets.UTF_8, options)) {
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

    /**
     * Gets the loaded {@link Configuration}.
     *
     * @return the loaded {@link Configuration}
     * @throws IllegalStateException if this configuration is not loaded
     */
    public @NotNull Configuration getLoadedConfiguration() {
        if (config != null) {
            return config;
        } else {
            throw new IllegalStateException("this configuration is not loaded");
        }
    }
}
