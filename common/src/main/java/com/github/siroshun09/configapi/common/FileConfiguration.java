package com.github.siroshun09.configapi.common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
     */
    void load() throws IOException;

    /**
     * Checks if the file is loaded.
     *
     * @return True if the file is loaded or false if it is not loaded.
     */
    boolean isLoaded();

    /**
     * Re-loads a yaml file.
     *
     * @see FileConfiguration#load()
     */
    default void reload() throws IOException {
        load();
    }

    /**
     * Saves to yaml file.
     * <p>
     * If a yaml file is not found, this method will create one.
     */
    void save() throws IOException;

    /**
     * Gets the path to a yaml file.
     *
     * @return The path to a yaml file.
     */
    @NotNull
    Path getPath();
}
