/*
 *     Copyright 2024 Siroshun09
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

package dev.siroshun.configapi.core.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class used for internal processing of this library
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * If the file does not exist in the specified path, creates its parent directory and it.
     *
     * @param path file path
     * @return given file path
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if the path is null
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Path createFileIfNotExists(@NotNull Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        if (!Files.exists(path)) {
            var parent = path.getParent();

            if (parent != null) {
                createDirectoriesIfNotExists(parent);
            }

            Files.createFile(path);
        }

        return path;
    }

    /**
     * If the directory does not exist in the specified path, creates its parent directory and it.
     *
     * @param path directory path
     * @return given file path
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if the path is null
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Path createDirectoriesIfNotExists(@NotNull Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        if (!Files.exists(path) || (!Files.isDirectory(path) && !Files.isSymbolicLink(path))) {
            Files.createDirectories(path);
        }

        return path;
    }
}
