package com.github.siroshun09.configapi.bungee;

import com.github.siroshun09.configapi.common.FileUtils;
import com.github.siroshun09.configapi.common.Yaml;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class implements {@link Yaml} on BungeeCord.
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
        config = null;
    }

    /**
     * Creates an {@link BungeeYaml} with no default values.
     *
     * @param filePath File path to load or save.
     * @param autoLoad True if automatically load, false otherwise.
     */
    public BungeeYaml(@NotNull Path filePath, boolean autoLoad) {
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
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), filePath.toFile());
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
    public @NotNull List<String> getStringList(@NotNull String path, @NotNull List<String> def) {
        List<String> list = isLoaded() ? config.getStringList(path) : Collections.emptyList();
        return !list.isEmpty() ? list : def;
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
    public Collection<String> getKeys() {
        return isLoaded() ? config.getKeys() : new LinkedHashSet<>();
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
        if (isLoaded()) {
            config.set(path, value);
        }
    }
}
