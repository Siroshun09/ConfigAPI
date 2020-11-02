/*
 *     Copyright 2020 Siroshun09
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

package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.util.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A factory class for creating {@link BukkitYaml}.
 */
public final class BukkitYamlFactory {

    private BukkitYamlFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link BukkitYaml} instance.
     * <p>
     * This method will not load the file.
     *
     * @param path the file path
     * @return {@link BukkitYaml} instance
     */
    public static @NotNull BukkitYaml getBukkitYaml(@NotNull Path path) {
        Objects.requireNonNull(path);
        return new BukkitYaml(path);
    }

    /**
     * Creates a loaded {@link BukkitYaml} instance.
     *
     * @param path the file path
     * @return loaded {@link BukkitYaml} instance
     * @throws IOException if an I/O error occurs
     */
    public static @NotNull BukkitYaml load(@NotNull Path path) throws IOException {
        BukkitYaml yaml = getBukkitYaml(path);

        yaml.load();

        return yaml;
    }

    /**
     * Creates a loaded {@link BukkitYaml} instance.
     * <p>
     * This method will copy from the plugin resource if the file does not exists,
     * or create the empty file if the file does not exists and the plugin resource was not found.
     *
     * @param plugin   the plugin instance
     * @param filePath the file path with the plugin directory as the root
     * @return loaded {@link BukkitYaml} instance
     * @throws IOException if an I/O error occurs
     * @see BukkitYamlFactory#createFilePath(Plugin, String)
     */
    public static @NotNull BukkitYaml load(@NotNull Plugin plugin, @NotNull String filePath) throws IOException {
        BukkitYaml yaml = getBukkitYaml(copyResourceIfNotExists(plugin, filePath));

        yaml.load();

        return yaml;
    }

    /**
     * Creates a loaded {@link BukkitYaml} instance.
     * <p>
     * If an {@link IOException} occurs, this method ignores it.
     *
     * @param path the file path
     * @return loaded {@link BukkitYaml} instance
     */
    public static @NotNull BukkitYaml loadUnsafe(@NotNull Path path) {
        BukkitYaml yaml = getBukkitYaml(path);

        try {
            yaml.load();
        } catch (IOException ignored) {
        }

        return yaml;
    }

    /**
     * Creates a loaded {@link BukkitYaml} instance.
     * <p>
     * If an {@link IOException} occurs, this method ignores it.
     *
     * @param plugin   the plugin instance
     * @param filePath the file path with the plugin directory as the root
     * @return loaded {@link BukkitYaml} instance
     * @see BukkitYamlFactory#createFilePath(Plugin, String)
     */
    public static @NotNull BukkitYaml loadUnsafe(@NotNull Plugin plugin, @NotNull String filePath) {
        Path path = createFilePath(plugin, filePath);

        try {
            copyResourceIfNotExists(plugin, filePath, path);
        } catch (IOException ignored) {
        }


        return loadUnsafe(path);
    }

    /**
     * Copies the plugin resource if the file does not exists.
     *
     * @param plugin   the plugin instance
     * @param filePath the file path with the plugin directory as the root
     * @return loaded {@link BukkitYaml} instance
     * @throws IOException if an I/O error occurs
     * @see BukkitYamlFactory#createFilePath(Plugin, String)
     */
    @NotNull
    public static Path copyResourceIfNotExists(@NotNull Plugin plugin, @NotNull String filePath) throws IOException {
        Path path = createFilePath(plugin, filePath);

        copyResourceIfNotExists(plugin, filePath, path);

        return path;
    }

    /**
     * Creates the file path.
     * <p>
     * The returns path is {@code SERVER_ROOT/plugins/PLUGIN_NAME/FILE_PATH}.
     *
     * @param plugin   the plugin instance
     * @param filePath the file path with the plugin directory as the root
     * @return the file path
     */
    @NotNull
    public static Path createFilePath(@NotNull Plugin plugin, @NotNull String filePath) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(filePath);

        if (filePath.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Path temp = plugin.getDataFolder().toPath();

        for (String path : filePath.split("/")) {
            if (path.isEmpty()) {
                throw new IllegalArgumentException();
            }

            temp = temp.resolve(path);
        }

        return temp;
    }

    private static void copyResourceIfNotExists(@NotNull Plugin plugin, @NotNull String filePath, @NotNull Path path) throws IOException {
        if (!Files.exists(path)) {
            FileUtils.createDirectoriesIfNotExists(path.getParent());

            try (InputStream resourceFile = plugin.getResource(filePath)) {
                if (resourceFile != null) {
                    Files.copy(resourceFile, path);
                }
            }
        }
    }
}
