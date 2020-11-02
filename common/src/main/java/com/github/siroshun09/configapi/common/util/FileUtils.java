package com.github.siroshun09.configapi.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
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
            createDirectoriesIfNotExists(path.getParent());
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
