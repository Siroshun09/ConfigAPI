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

package dev.siroshun.configapi.test.shared.util;

import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An utility class to create {@link Node}s.
 */
public final class NodeFactory {

    /**
     * Creates a {@link MapNode}.
     *
     * @param builder a {@link Consumer} to edit {@link MapNode}
     * @return a created {@link MapNode}
     */
    public static @NotNull MapNode mapNode(@NotNull Consumer<MapNode> builder) {
        var mapNode = MapNode.create();
        builder.accept(mapNode);
        return mapNode;
    }

    private NodeFactory() {
        throw new UnsupportedOperationException();
    }
}
