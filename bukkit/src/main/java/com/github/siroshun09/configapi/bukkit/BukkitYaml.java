package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.util.FileUtils;
import com.github.siroshun09.configapi.common.yaml.AbstractYaml;
import com.github.siroshun09.configapi.common.yaml.Yaml;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements {@link Yaml} on Bukkit.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public class BukkitYaml extends AbstractYaml {

    protected YamlConfiguration config = new YamlConfiguration();
    private boolean isLoaded = false;

    /**
     * Creates a {@link BukkitYaml} with no default values.
     *
     * @param filePath the file to load or save.
     */
    public BukkitYaml(@NotNull Path filePath) {
        super(filePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws IOException {
        FileUtils.createFileIfNotExists(filePath);

        if (Files.isRegularFile(filePath) && Files.isReadable(filePath)) {
            config = YamlConfiguration.loadConfiguration(filePath.toFile());
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
            config.save(filePath.toFile());
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
     * Gets {@link YamlConfiguration}.
     *
     * @return {@link YamlConfiguration}
     */
    @NotNull
    public YamlConfiguration getConfig() {
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
        return config.getIntegerList(path);
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
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Set<String> getKeys() {
        return config.getKeys(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path, "path must not be null.");
        config.set(path, value);
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

        ItemStack item = config.getItemStack(path);
        return item != null ? item : def;
    }
}
