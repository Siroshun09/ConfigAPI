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

package com.github.siroshun09.configapi.core.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * A utility class that provides methods to copy files contained in a jar.
 */
public final class ResourceUtils {

    /**
     * Copies a file from classloader.
     *
     * @param loader the classloader
     * @param name   the filename
     * @param target the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromClassLoader(@NotNull ClassLoader loader,
                                           @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(loader);
        Objects.requireNonNull(name);
        Objects.requireNonNull(target);

        copy(() -> getInputStreamFromClassLoader(loader, name), target);
    }

    /**
     * Copies a file from classloader if the {@code target} not exists.
     *
     * @param loader the classloader
     * @param name   the filename
     * @param target the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     * @see #copyFromClassLoader(ClassLoader, String, Path)
     */
    public static void copyFromClassLoaderIfNotExists(@NotNull ClassLoader loader,
                                                      @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(loader);
        Objects.requireNonNull(name);
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copy(() -> getInputStreamFromClassLoader(loader, name), target);
        }
    }

    /**
     * Copies a file from jar.
     *
     * @param jar    the jar file
     * @param name   the filename
     * @param target the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromJar(@NotNull JarFile jar,
                                   @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(jar);
        Objects.requireNonNull(name);
        Objects.requireNonNull(target);

        copy(() -> getInputStreamFromJar(jar, name), target);
    }

    /**
     * Copies a file from jar if the {@code target} not exists.
     *
     * @param jar    the jar file
     * @param name   the filename
     * @param target the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromJarIfNotExists(@NotNull JarFile jar,
                                              @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(jar);
        Objects.requireNonNull(name);
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copy(() -> getInputStreamFromJar(jar, name), target);
        }
    }

    /**
     * Copies a file from jar.
     *
     * @param jarPath the jar filepath
     * @param name    the filename
     * @param target  the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromJar(@NotNull Path jarPath,
                                   @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(jarPath);

        if (!Files.exists(jarPath)) {
            return;
        }

        try (var jar = new JarFile(jarPath.toFile(), false)) {
            copyFromJar(jar, name, target);
        }
    }

    /**
     * Copies a file from jar if the {@code target} not exists.
     *
     * @param jarPath the jar filepath
     * @param name    the filename
     * @param target  the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromJarIfNotExists(@NotNull Path jarPath,
                                              @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copyFromJar(jarPath, name, target);
        }
    }

    /**
     * Gets an {@link InputStream} from the {@link ClassLoader}.
     *
     * @param classLoader the {@link ClassLoader} to get an {@link InputStream}
     * @param name        the resource name
     * @return {@link ClassLoader#getResourceAsStream(String)}
     * @throws IllegalStateException if the resource was not found
     * @throws NullPointerException  if {@code null} is specified as an argument.
     */
    public static @NotNull InputStream getInputStreamFromClassLoader(@NotNull ClassLoader classLoader,
                                                                     @NotNull String name) {
        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(name);

        var input = classLoader.getResourceAsStream(name);

        if (input != null) {
            return input;
        } else {
            throw new IllegalStateException(name + " was not found in the classloader");
        }
    }

    /**
     * Gets an {@link InputStream} from the {@link JarFile}.
     *
     * @param jar  the {@link JarFile} to get an {@link InputStream}
     * @param name the resource name
     * @return the {@link InputStream} from {@link JarFile#getInputStream(ZipEntry)}
     * @throws IOException           if an I/O error occurs
     * @throws IllegalStateException if the resource was not found
     * @throws NullPointerException  if {@code null} is specified as an argument.
     */
    public static @NotNull InputStream getInputStreamFromJar(@NotNull JarFile jar,
                                                             @NotNull String name) throws IOException {
        Objects.requireNonNull(jar);
        Objects.requireNonNull(name);

        var file = jar.getEntry(name);

        if (file != null) {
            return jar.getInputStream(file);
        } else {
            throw new IllegalStateException(name + " was not found in the jar");
        }
    }

    private static void copy(@NotNull IOSupplier<InputStream> inputSupplier, @NotNull Path target) throws IOException {
        var parent = target.getParent();

        if (parent != null) {
            FileUtils.createDirectoriesIfNotExists(parent);
        }

        try (var input = inputSupplier.get()) {
            if (input != null) {
                Files.copy(input, target);
            }
        }
    }

    @FunctionalInterface
    private interface IOSupplier<T> {

        T get() throws IOException;

    }

    private ResourceUtils() {
        throw new UnsupportedOperationException();
    }
}
