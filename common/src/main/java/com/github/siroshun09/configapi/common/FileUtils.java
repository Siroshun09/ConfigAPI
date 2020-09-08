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
     * Check if the parent directory of the given path exists.
     * <p>
     * If the parent directory is not found, this method will create one.
     *
     * @param path File path to check.
     * @return True if it finally exists, false otherwise.
     */
    public static boolean checkParentDirectory(@NotNull Path path) {
        Objects.requireNonNull(path);

        Path dir = path.getParent();
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Check that the file exists and is readable and writable.
     * <p>
     * If the file is not found, this method will create one.
     *
     * @param path File path to check.
     * @return True if it finally exists and is readable and writable, false otherwise.
     */
    public static boolean checkFile(@NotNull Path path) {
        Objects.requireNonNull(path);

        if (!Files.exists(path) && checkParentDirectory(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return Files.isRegularFile(path) && Files.isReadable(path) && Files.isWritable(path);
    }

    /**
     * If the file does not exist in the specified path, creates its parent directory and it.
     *
     * @param path file path
     * @return given file path
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if the path is null
     */
    public static Path createFileIfNotExists(@NotNull Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        if (!Files.exists(path)) {
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
