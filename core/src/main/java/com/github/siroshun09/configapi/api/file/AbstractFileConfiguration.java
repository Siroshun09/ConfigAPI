/*
 *     Copyright 2023 Siroshun09
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

import com.github.siroshun09.configapi.api.AbstractConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * An abstract implementation of {@link FileConfiguration}.
 */
public abstract class AbstractFileConfiguration extends AbstractConfiguration implements FileConfiguration {

    private final Path path;
    private boolean isLoaded;

    /**
     * Creates {@link AbstractFileConfiguration}.
     *
     * @param path the filepath
     */
    protected AbstractFileConfiguration(@NotNull Path path) {
        this.path = Objects.requireNonNull(path);
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void reload() throws IOException {
        isLoaded = false;
        load();
    }

    @Override
    public @NotNull Path getPath() {
        return path;
    }

    /**
     * Sets whether the file was loaded or not.
     *
     * @param isLoaded {@code true} if the file is loaded or {@code false} if it is not loaded
     */
    protected final void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
}
