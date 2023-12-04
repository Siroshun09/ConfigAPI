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

package com.github.siroshun09.configapi.format.jackson;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.MapNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JacksonFormat implements FileFormat<MapNode> {

    public static final JacksonFormat DEFAULT = new JacksonFormat(new ObjectMapper());

    public static final JacksonFormat PRETTY_PRINTING = new JacksonFormat(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).setDefaultPrettyPrinter(createDefaultPrettyPrinter()));

    @Contract(" -> new")
    public static @NotNull DefaultPrettyPrinter createDefaultPrettyPrinter() {
        var printer = new DefaultPrettyPrinter(createSeparators());
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        return printer;
    }

    private static @NotNull Separators createSeparators() {
        return Separators.createDefaultInstance()
                .withObjectFieldValueSpacing(Separators.Spacing.AFTER);
    }

    private final ObjectMapper objectMapper;

    public JacksonFormat(@NotNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.registerModule(NodeSerialization.createModule());
    }

    @Override
    public @NotNull MapNode load(@NotNull Path filepath) throws IOException {
        if (Files.isRegularFile(filepath)) {
            return this.load(Files.newBufferedReader(filepath, StandardCharsets.UTF_8));
        } else {
            return MapNode.create();
        }
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        return this.objectMapper.readValue(reader, MapNode.class);
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        this.objectMapper.writeValue(writer, node);
    }
}
