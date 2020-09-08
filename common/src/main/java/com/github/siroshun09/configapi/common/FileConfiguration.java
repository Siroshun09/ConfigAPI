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
     * Loads from the file.
     */
    void load() throws IOException;

    /**
     * Checks if the file is loaded.
     *
     * @return {@code true} if the file is loaded or {@code false} if it is not loaded.
     */
    boolean isLoaded();

    /**
     * Re-loads from the file.
     *
     * @see FileConfiguration#load()
     */
    default void reload() throws IOException {
        load();
    }

    /**
     * Saves to the file.
     */
    void save() throws IOException;

    /**
     * Gets the path to the file.
     *
     * @return the path to the file.
     */
    @NotNull
    Path getPath();
}
