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

package com.github.siroshun09.configapi.api.file;

import com.github.siroshun09.configapi.api.Configuration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An interface that represents the {@link Configuration} that can load from a file.
 */
public interface FileConfiguration extends Configuration, AutoCloseable {

    /**
     * Loads from the file.
     *
     * @throws IOException if an I/O error occurs
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
     * @throws IOException if an I/O error occurs
     * @see FileConfiguration#load()
     */
    void reload() throws IOException;

    /**
     * Saves to the file.
     *
     * @throws IOException if an I/O error occurs
     */
    void save() throws IOException;

    /**
     * Gets the path to the file.
     *
     * @return the path to the file.
     */
    @NotNull Path getPath();

    /**
     * Closes this {@link FileConfiguration}.
     * <p>
     * By default, this method calls {@link #clear()}.
     *
     * @see AutoCloseable
     */
    @Override
    default void close() {
        clear();
    }
}
