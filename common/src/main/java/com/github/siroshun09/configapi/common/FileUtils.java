package com.github.siroshun09.configapi.common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class used for internal processing of this library
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
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

    /**
     * Deletes the file.
     *
     * @param path file path
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if the path is null
     */
    public static void deleteFile(@NotNull Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        if (Files.exists(path) && Files.isRegularFile(path)) {
            Files.delete(path);
        }
    }

    /**
     * Deletes the given directory and all directories and files under it.
     *
     * @param path given directory path
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if the path is null
     */
    public static void deleteDirectory(@NotNull Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return;
        }

        try (DirectoryStream<Path> contents = Files.newDirectoryStream(path)) {
            for (Path file : contents) {
                if (Files.isDirectory(file)) {
                    deleteDirectory(file);
                } else {
                    Files.delete(file);
                }
            }
        }

        Files.delete(path);
    }
}
