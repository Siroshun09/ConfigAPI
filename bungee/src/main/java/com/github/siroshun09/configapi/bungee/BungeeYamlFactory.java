package com.github.siroshun09.configapi.bungee;

import com.github.siroshun09.configapi.common.FileUtils;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class BungeeYamlFactory {

    private BungeeYamlFactory() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull BungeeYaml getBungeeYaml(@NotNull Path path) {
        Objects.requireNonNull(path);
        return new BungeeYaml(path);
    }

    public static @NotNull BungeeYaml load(@NotNull Path path) throws IOException {
        BungeeYaml yaml = getBungeeYaml(path);

        yaml.load();

        return yaml;
    }

    public static @NotNull BungeeYaml load(@NotNull Plugin plugin, @NotNull String filePath) throws IOException {
        BungeeYaml yaml = getBungeeYaml(copyResourceIfNotExists(plugin, filePath));

        yaml.load();

        return yaml;
    }

    public static @NotNull BungeeYaml loadUnsafe(@NotNull Plugin plugin, @NotNull String filePath) {
        Path path = createFilePath(plugin, filePath);

        try {
            copyResourceIfNotExists(plugin, filePath, path);
        } catch (IOException ignored) {
        }


        return loadUnsafe(path);
    }

    public static @NotNull BungeeYaml loadUnsafe(@NotNull Path path) {
        BungeeYaml yaml = getBungeeYaml(path);

        try {
            yaml.load();
        } catch (IOException ignored) {
        }

        return yaml;
    }

    @NotNull
    public static Path copyResourceIfNotExists(@NotNull Plugin plugin, @NotNull String filePath) throws IOException {
        Path path = createFilePath(plugin, filePath);

        copyResourceIfNotExists(plugin, filePath, path);

        return path;
    }

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

            try (InputStream resourceFile = plugin.getResourceAsStream(filePath)) {
                if (resourceFile != null) {
                    Files.copy(resourceFile, path);
                }
            }
        }
    }
}
