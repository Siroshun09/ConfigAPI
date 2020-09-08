package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.FileUtils;
import com.github.siroshun09.configapi.common.Yaml;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements {@link Yaml} on Bukkit.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public class BukkitYaml implements Yaml {

    protected final Path filePath;
    protected YamlConfiguration config;

    /**
     * Creates an {@link BukkitYaml} with no default values.
     * <p>
     * This constructor loads the file automatically.
     *
     * @param filePath File path to load or save.
     */
    public BukkitYaml(@NotNull Path filePath) {
        this(filePath, true);
    }

    /**
     * Creates an {@link BukkitYaml} with no default values.
     *
     * @param filePath File path to load or save.
     * @param autoLoad True if automatically load, false otherwise.
     */
    public BukkitYaml(@NotNull Path filePath, boolean autoLoad) {
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
            config = YamlConfiguration.loadConfiguration(filePath.toFile());
            return true;
        } else {
            return false;
        }
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
                getConfig().save(filePath.toFile());
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
     * Gets {@link YamlConfiguration}.
     *
     * @return {@link YamlConfiguration}
     * @throws IllegalStateException Throws when a yaml file is not loaded.
     */
    @NotNull
    public YamlConfiguration getConfig() throws IllegalStateException {
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
        return isLoaded() ? config.getIntegerList(path) : def;
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
     * Gets the requested {@link ItemStack} by path.
     * <p>
     * If the {@link ItemStack} could not be obtained, this method returns {@code new ItemStack(Material.AIR)}.
     *
     * @param path Path of the {@link ItemStack} to get.
     * @return Requested {@link ItemStack}.
     * @since 1.5
     */
    @NotNull
    public ItemStack getItemStack(@NotNull String path) {
        return getItemStack(path, new ItemStack(Material.AIR));
    }

    /**
     * Gets the requested {@link ItemStack} by path.
     *
     * @param path Path of the {@link ItemStack} to get.
     * @param def  The default {@link ItemStack} to return if the {@link ItemStack} could not be obtained.
     * @return Requested {@link ItemStack}.
     * @since 1.5
     */
    @NotNull
    public ItemStack getItemStack(@NotNull String path, @NotNull ItemStack def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");

        if (isLoaded()) {
            ItemStack item = config.getItemStack(path);
            return item != null ? item : def;
        } else {
            return def;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Set<String> getKeys() {
        return isLoaded() ? config.getKeys(false) : new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path, "path must not be null.");

        if (isLoaded()) {
            config.set(path, value);
        }
    }
}
