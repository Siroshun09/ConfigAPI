package com.github.siroshun09.configapi.common;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * An interface that extends {@link Configuration} to get from a file.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public interface FileConfiguration extends Configuration {

    /**
     * Loads a yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     *
     * @return True if the load is successful or false if it is failure.
     */
    boolean load();

    /**
     * Checks if the file is loaded.
     *
     * @return True if the file is loaded or false if it is not loaded.
     */
    boolean isLoaded();

    /**
     * Reload a yaml file.
     *
     * @return True if the reload is successful or false if it is failure.
     * @see FileConfiguration#load()
     */
    default boolean reload() {
        return load();
    }

    /**
     * Save to yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     *
     * @return True if the save is successful or false if it is failure.
     */
    boolean save();

    /**
     * Get the path to a yaml file.
     *
     * @return The path to a yaml file.
     */
    @NotNull
    Path getPath();
}
