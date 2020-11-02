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

package com.github.siroshun09.configapi.bungee;

import com.github.siroshun09.configapi.common.util.FileUtils;
import com.github.siroshun09.configapi.common.yaml.AbstractYaml;
import com.github.siroshun09.configapi.common.yaml.Yaml;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This class that implements {@link Yaml} on BungeeCord.
 */
public class BungeeYaml extends AbstractYaml {

    private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Configuration config = new Configuration();
    private boolean isLoaded = false;

    /**
     * Creates a {@link BungeeYaml}.
     *
     * @param filePath the file path to load or save.
     */
    public BungeeYaml(@NotNull Path filePath) {
        super(filePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws IOException {
        FileUtils.createFileIfNotExists(filePath);

        if (Files.isRegularFile(filePath) && Files.isReadable(filePath)) {
            config = PROVIDER.load(filePath.toFile());
            isLoaded = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        FileUtils.createFileIfNotExists(filePath);

        if (Files.isRegularFile(filePath) && Files.isWritable(filePath)) {
            PROVIDER.save(config, filePath.toFile());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Path getPath() {
        return filePath;
    }

    /**
     * Gets {@link Configuration}.
     *
     * @return {@link Configuration}
     * @throws IllegalStateException Throws when a yaml file is not loaded.
     */
    @NotNull
    public Configuration getConfig() throws IllegalStateException {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Object get(@NotNull String path) {
        return config.get(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        Objects.requireNonNull(path, "path must not be null.");
        return config.getBoolean(path, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(@NotNull String path, double def) {
        Objects.requireNonNull(path, "path must not be null.");
        return config.getDouble(path, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInteger(@NotNull String path, int def) {
        Objects.requireNonNull(path, "path must not be null.");
        return config.getInt(path, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(@NotNull String path, long def) {
        Objects.requireNonNull(path, "path must not be null.");
        return config.getLong(path, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getString(@NotNull String path, @NotNull String def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");

        String value = config.getString(path, def);
        return value != null ? value : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<String> getStringList(@NotNull String path, @NotNull List<String> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getStringList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Short> getShortList(@NotNull String path, @NotNull List<Short> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getShortList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getIntList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Long> getLongList(@NotNull String path, @NotNull List<Long> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getLongList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getFloatList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Double> getDoubleList(@NotNull String path, @NotNull List<Double> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return config.getDoubleList(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Collection<String> getKeys() {
        return config.getKeys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path, "path must not be null.");
        config.set(path, value);
    }
}
