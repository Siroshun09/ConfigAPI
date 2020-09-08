package com.github.siroshun09.configapi.bungee;

import com.github.siroshun09.configapi.common.FileUtils;
import com.github.siroshun09.configapi.common.yaml.Yaml;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * This class implements {@link Yaml} on BungeeCord.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public class BungeeYaml implements Yaml {

    protected final Path filePath;
    protected Configuration config;

    /**
     * Creates an {@link BungeeYaml} with no default values.
     * <p>
     * This constructor loads the file automatically.
     *
     * @param filePath File path to load or save.
     */
    public BungeeYaml(@NotNull Path filePath) {
        this(filePath, true);
    }

    /**
     * Creates an {@link BungeeYaml} with no default values.
     *
     * @param filePath File path to load or save.
     * @param autoLoad True if automatically load, false otherwise.
     */
    public BungeeYaml(@NotNull Path filePath, boolean autoLoad) {
        Objects.requireNonNull(filePath, "filePath must not be null.");

        this.filePath = filePath;
        if (autoLoad) load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        if (FileUtils.checkFile(filePath)) {
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(filePath.toFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded() {
        return config != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save() {
        try {
            if (FileUtils.checkFile(filePath)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), filePath.toFile());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        if (isLoaded()) {
            return config;
        } else {
            throw new IllegalStateException("A yaml file is not loaded.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getBoolean(path, def) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(@NotNull String path, double def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getDouble(path, def) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(@NotNull String path, int def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getInt(path, def) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(@NotNull String path, long def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getLong(path, def) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getString(@NotNull String path, @NotNull String def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");

        String value = isLoaded() ? config.getString(path, def) : null;
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
        return isLoaded() ? config.getStringList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Short> getShortList(@NotNull String path, @NotNull List<Short> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getShortList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getIntList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Long> getLongList(@NotNull String path, @NotNull List<Long> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getLongList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getFloatList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<Double> getDoubleList(@NotNull String path, @NotNull List<Double> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getDoubleList(path) : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Collection<String> getKeys() {
        return isLoaded() ? config.getKeys() : new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path, "path must not be null.");
        if (isLoaded()) config.set(path, value);
    }
}
