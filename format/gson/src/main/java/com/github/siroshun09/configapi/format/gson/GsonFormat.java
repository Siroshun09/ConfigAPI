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

package com.github.siroshun09.configapi.format.gson;

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.CommentedNode;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A {@link FileFormat} implementation that loading/saving {@link MapNode} from/to json files using {@link Gson}.
 * <p>
 * Supported {@link Node}s:
 *
 * <ul>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ValueNode}s
 *     <ul>
 *         <li>{@link EnumValue} will be written as {@link String} using {@link Enum#name()}</li>
 *         <li>Loading {@link EnumValue} is not supported</li>
 *     </ul>
 *     </li>
 *     <li>{@link MapNode} and {@link ListNode}</li>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ArrayNode}s: serialize only</li>
 *     <li>{@link NullNode}</li>
 *     <li>{@link CommentedNode} - The comment will be dropped</li>
 * </ul>
 */
public final class GsonFormat implements FileFormat<MapNode> {

    /**
     * An instance of {@link GsonFormat} that created from a plain {@link GsonBuilder}.
     */
    public static final GsonFormat DEFAULT = new GsonFormat(new GsonBuilder());

    /**
     * An instance of {@link GsonFormat} that created from a {@link GsonBuilder} that set pretty printing.
     */
    public static final GsonFormat PRETTY_PRINTING = new GsonFormat(new GsonBuilder().setPrettyPrinting());

    private final Gson gson;

    /**
     * Creates a new {@link GsonFormat} from the {@link GsonBuilder}.
     *
     * @param gsonBuilder the {@link GsonBuilder}
     */
    public GsonFormat(@NotNull GsonBuilder gsonBuilder) {
        this.gson = gsonBuilder.registerTypeAdapter(MapNode.class, NodeSerializer.INSTANCE).create();
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        try {
            var node = this.gson.fromJson(reader, MapNode.class);
            return node != null ? node : MapNode.create();
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        try {
            this.gson.toJson(node, MapNode.class, writer);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }
}
