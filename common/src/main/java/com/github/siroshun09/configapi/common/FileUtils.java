package com.github.siroshun09.configapi.common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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
}
