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

package dev.siroshun.configapi.core.file;

import dev.siroshun.configapi.core.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * An interface for loading/saving {@link Node}s from/to files.
 *
 * @param <N> a type of root node
 */
public interface FileFormat<N extends Node<?>> {

    /**
     * Loads a node from a {@link Reader}.
     *
     * @param reader a {@link Reader} to load a node
     * @return a loaded {@link Node} ({@link N})
     * @throws IOException if I/O error occurred
     */
    @NotNull N load(@NotNull Reader reader) throws IOException;

    /**
     * Loads a node from a file.
     *
     * @param filepath a filepath to load a node
     * @return a loaded {@link Node} ({@link N})
     * @throws IOException if I/O error occurred
     */
    default @NotNull N load(@NotNull Path filepath) throws IOException {
        Objects.requireNonNull(filepath);
        try (var reader = Files.isRegularFile(filepath) ? Files.newBufferedReader(filepath, StandardCharsets.UTF_8) : Reader.nullReader()) {
            return this.load(reader);
        }
    }

    /**
     * Loads a node from a {@link InputStream}.
     *
     * @param input a {@link InputStream} to load a node
     * @return a loaded {@link Node} ({@link N})
     * @throws IOException if I/O error occurred
     */
    default @NotNull N load(@NotNull InputStream input) throws IOException {
        Objects.requireNonNull(input);
        try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return this.load(reader);
        }
    }

    /**
     * Saves a node to a {@link Writer}.
     *
     * @param node   a root {@link Node} ({@link N}) to save
     * @param writer a {@link Writer} to write a node
     * @throws IOException if I/O error occurred
     */
    void save(@NotNull N node, @NotNull Writer writer) throws IOException;

    /**
     * Saves a node to a file.
     *
     * @param node     a root {@link Node} ({@link N}) to save
     * @param filepath a filepath to write a node
     * @throws IOException if I/O error occurred
     */
    default void save(@NotNull N node, @NotNull Path filepath) throws IOException {
        var parent = filepath.getParent();

        if (parent != null && !Files.isDirectory(parent)) {
            Files.createDirectories(parent);
        }

        try (var writer = Files.newBufferedWriter(filepath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            this.save(node, writer);
        }
    }

    /**
     * Saves a node to a {@link OutputStream}.
     *
     * @param node   a root {@link Node} ({@link N}) to save
     * @param output a {@link OutputStream} to write a node
     * @throws IOException if I/O error occurred
     */
    default void save(@NotNull N node, @NotNull OutputStream output) throws IOException {
        try (var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            this.save(node, writer);
        }
    }

}
