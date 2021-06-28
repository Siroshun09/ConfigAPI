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

package com.github.siroshun09.configapi.api.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.JarFile;

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

        try (InputStream input = loader.getResourceAsStream(name)) {
            if (input != null) {
                Files.copy(input, target);
            }
        }
    }

    /**
     * Copies a file from classloader if the {@code target} not exists.
     *
     * @param loader the classloader
     * @param name   the filename
     * @param target the filepath to save
     * @throws IOException          if an I/O error occurs
     * @throws NullPointerException if {@code null} is specified as an argument.
     */
    public static void copyFromClassLoaderIfNotExists(@NotNull ClassLoader loader,
                                                      @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copyFromClassLoader(loader, name, target);
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

        var file = jar.getEntry(name);

        if (file == null) {
            return;
        }

        try (var input = jar.getInputStream(file)) {
            Files.copy(input, target);
        }
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
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copyFromJar(jar, name, target);
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

        var jar = new JarFile(jarPath.toFile(), false);
        copyFromJar(jar, name, target);
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
}
