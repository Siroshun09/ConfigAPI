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

package dev.siroshun.configapi.format.properties;

import dev.siroshun.configapi.core.file.FileFormat;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.StringRepresentable;
import dev.siroshun.configapi.core.node.StringValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;
import java.util.Properties;

/**
 * A {@link FileFormat} implementation that loading/saving {@link MapNode} from/to properties files.
 * <p>
 * When loading from properties file, all keys/values will store as {@link String}/{@link StringValue}.
 * <p>
 * Saving to properties file supports {@link StringRepresentable} nodes and {@link CommentedNode} with {@link StringRepresentable} node.
 * Other {@link Node} types will throw {@link IllegalArgumentException}.
 */
public final class PropertiesFormat implements FileFormat<MapNode> {

    /**
     * A default instance of {@link PropertiesFormat}.
     */
    public static final PropertiesFormat DEFAULT = new PropertiesFormat();

    private PropertiesFormat() {
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        Objects.requireNonNull(reader);
        var collector = new CollectToMapNode();
        collector.load(reader);
        return collector.mapNode;
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        Objects.requireNonNull(node);
        Objects.requireNonNull(writer);

        for (var entry : node.value().entrySet()) {
            var key = entry.getKey();
            appendEscapedString(String.valueOf(key), true, writer);

            writer.write('=');

            var value = entry.getValue();
            String stringRepresentation;

            if (value instanceof StringRepresentable stringRepresentable) {
                stringRepresentation = stringRepresentable.asString();
            } else if (value instanceof CommentedNode<?> commentedNode && commentedNode.node() instanceof StringRepresentable stringRepresentable) {
                stringRepresentation = stringRepresentable.asString();
            } else {
                throw new IllegalArgumentException("The given MapNode has non-string-representable nodes.");
            }

            appendEscapedString(stringRepresentation, false, writer);
            writer.write(System.lineSeparator());
        }
    }

    private static void appendEscapedString(@NotNull String str, boolean escapeSpace, @NotNull Writer writer) throws IOException {
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (61 < c && c < 127) {
                if (c == '\\') writer.append('\\').append('\\');
                else writer.append(c);
                continue;
            }

            switch (c) {
                case ' ' -> {
                    if (i == 0 || escapeSpace) writer.append('\\');
                    writer.append(' ');
                }
                case '\t' -> writer.append('\\').append('t');
                case '\n' -> writer.append('\\').append('n');
                case '\r' -> writer.append('\\').append('r');
                case '\f' -> writer.append('\\').append('f');
                case '=', ':', '#', '!' -> writer.append('\\').append(c);
                default -> writer.append(c);
            }
        }
    }

    private static final class CollectToMapNode extends Properties {

        private final MapNode mapNode = MapNode.create();

        @Override
        public synchronized Object put(Object key, Object value) {
            return this.mapNode.set(key, value);
        }
    }
}
