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
import java.util.ArrayList;
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
        config = null;
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
     * Loads a yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     *
     * @return True if the load is successful or false if it is failure.
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
     * Checks if the file is loaded.
     *
     * @return True if the file is loaded or false if it is not loaded.
     */
    @Override
    public boolean isLoaded() {
        return config != null;
    }

    /**
     * Save to yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     *
     * @return True if the save is successful or false if it is failure.
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
     * Get the path to a yaml file.
     *
     * @return The path to a yaml file.
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
     * Gets the requested boolean by path.
     * <p>
     * If the value could not be obtained, this method returns {@code false}.
     *
     * @param path Path of the boolean to get.
     * @return Requested boolean.
     */
    @Override
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    /**
     * Gets the requested boolean by path.
     *
     * @param path Path of the boolean to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested boolean.
     */
    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getBoolean(path, def) : def;
    }

    /**
     * Gets the requested double by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the double to get.
     * @return Requested double.
     */
    @Override
    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    /**
     * Gets the requested double by path.
     *
     * @param path Path of the double to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested double.
     */
    @Override
    public double getDouble(@NotNull String path, double def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getDouble(path, def) : def;
    }

    /**
     * Gets the requested integer by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the integer to get.
     * @return Requested integer.
     */
    @Override
    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    /**
     * Gets the requested integer by path.
     *
     * @param path Path of the integer to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested integer.
     */
    @Override
    public int getInt(@NotNull String path, int def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getInt(path, def) : def;
    }

    /**
     * Gets the requested long by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the long to get.
     * @return Requested long.
     */
    @Override
    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    /**
     * Gets the requested long by path.
     *
     * @param path Path of the long to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested long.
     */
    @Override
    public long getLong(@NotNull String path, long def) {
        Objects.requireNonNull(path, "path must not be null.");
        return isLoaded() ? config.getLong(path, def) : def;
    }

    /**
     * Gets the requested string by path.
     * <p>
     * If the value could not be obtained, this method returns an empty string.
     *
     * @param path Path of the string to get.
     * @return Requested string.
     */
    @Override
    @NotNull
    public String getString(@NotNull String path) {
        return getString(path, "");
    }

    /**
     * Gets the requested string by path.
     *
     * @param path Path of the string to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested string.
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
     * Gets the requested string list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty string list.
     *
     * @param path Path of the string list to get.
     * @return Requested string list.
     */
    @Override
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return getStringList(path, new ArrayList<>());
    }

    /**
     * Gets the requested string list by path.
     *
     * @param path Path of the string list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested string list.
     */
    @Override
    @NotNull
    public List<String> getStringList(@NotNull String path, @NotNull List<String> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getStringList(path) : def;
    }

    /**
     * Gets the requested short list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty short list.
     *
     * @param path Path of the short list to get.
     * @return Requested short list.
     */
    @Override
    @NotNull
    public List<Short> getShortList(@NotNull String path) {
        return getShortList(path, new ArrayList<>());
    }

    /**
     * Gets the requested short list by path.
     *
     * @param path Path of the short list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested short list.
     */
    @Override
    @NotNull
    public List<Short> getShortList(@NotNull String path, @NotNull List<Short> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getShortList(path) : def;
    }

    /**
     * Gets the requested integer list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty integer list.
     *
     * @param path Path of the integer list to get.
     * @return Requested integer list.
     */
    @Override
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path) {
        return getIntegerList(path, new ArrayList<>());
    }

    /**
     * Gets the requested integer list by path.
     *
     * @param path Path of the integer list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested integer list.
     */
    @Override
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getIntegerList(path) : def;
    }

    /**
     * Gets the requested long list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty long list.
     *
     * @param path Path of the long list to get.
     * @return Requested long list.
     */
    @Override
    @NotNull
    public List<Long> getLongList(@NotNull String path) {
        return getLongList(path, new ArrayList<>());
    }

    /**
     * Gets the requested long list by path.
     *
     * @param path Path of the long list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested long list.
     */
    @Override
    @NotNull
    public List<Long> getLongList(@NotNull String path, @NotNull List<Long> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getLongList(path) : def;
    }

    /**
     * Gets the requested float list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty float list.
     *
     * @param path Path of the float list to get.
     * @return Requested float list.
     */
    @Override
    @NotNull
    public List<Float> getFloatList(@NotNull String path) {
        return getFloatList(path, new ArrayList<>());
    }

    /**
     * Gets the requested float list by path.
     *
     * @param path Path of the float list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested float list.
     */
    @Override
    @NotNull
    public List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");
        return isLoaded() ? config.getFloatList(path) : def;
    }

    /**
     * Gets the requested double list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty double list.
     *
     * @param path Path of the double list to get.
     * @return Requested double list.
     */
    @Override
    @NotNull
    public List<Double> getDoubleList(@NotNull String path) {
        return getDoubleList(path, new ArrayList<>());
    }

    /**
     * Gets the requested double list by path.
     *
     * @param path Path of the double list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested double list.
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
     * Gets a set containing keys in this yaml file.
     * <p>
     * The returned set does not include deep key.
     *
     * @return Set of keys contained within this yaml file.
     */
    @Override
    @NotNull
    public Set<String> getKeys() {
        return isLoaded() ? config.getKeys(false) : new LinkedHashSet<>();
    }

    /**
     * Set the value to the specified path.
     * <p>
     * If given value is null, the path will be removed.
     *
     * @param path  Path of the object to set.
     * @param value New value to set the path to.
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        Objects.requireNonNull(path, "path must not be null.");

        if (isLoaded()) {
            config.set(path, value);
        }
    }
}
