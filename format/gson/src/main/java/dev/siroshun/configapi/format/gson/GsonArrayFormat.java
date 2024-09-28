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

package dev.siroshun.configapi.format.gson;

import dev.siroshun.configapi.core.file.FileFormat;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.ArrayNode;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NullNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.ValueNode;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link FileFormat} implementation that loading/saving {@link ListNode} from/to json files using {@link Gson}.
 * <p>
 * Supported {@link Node}s:
 *
 * <ul>
 *     <li>{@link ValueNode}s
 *     <ul>
 *         <li>{@link CharValue} and {@link CharArray} is written as {@link String}</li>
 *         <li>{@link EnumValue} will be written as {@link String} using {@link Enum#name()}</li>
 *         <li>Loading {@link CharValue}, {@link CharArray}, and {@link EnumValue} is not supported</li>
 *     </ul>
 *     </li>
 *     <li>{@link MapNode} and {@link ListNode}</li>
 *     <li>{@link ArrayNode}s: serialize only</li>
 *     <li>{@link NullNode}</li>
 *     <li>{@link CommentedNode} - The comment will be dropped</li>
 * </ul>
 */
public final class GsonArrayFormat extends AbstractGsonFormat<ListNode> {

    /**
     * An instance of {@link GsonArrayFormat} that created from a plain {@link GsonBuilder}.
     */
    public static final GsonArrayFormat DEFAULT = new GsonArrayFormat(new GsonBuilder());

    /**
     * An instance of {@link GsonArrayFormat} that created from a {@link GsonBuilder} that set pretty printing.
     */
    public static final GsonArrayFormat PRETTY_PRINTING = new GsonArrayFormat(new GsonBuilder().setPrettyPrinting());

    /**
     * Creates a new {@link GsonFormat} from the {@link GsonBuilder}.
     *
     * @param gsonBuilder the {@link GsonBuilder}
     */
    public GsonArrayFormat(@NotNull GsonBuilder gsonBuilder) {
        super(gsonBuilder, ListNode.class, NodeAdapter.LIST_NODE_ADAPTER);
    }

    @Override
    protected @NotNull ListNode createEmptyNode() {
        return ListNode.create();
    }
}
