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

package com.github.siroshun09.configapi.test.shared.data;

import com.github.siroshun09.configapi.core.node.MapNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * A set of sample data for testing.
 */
public final class Samples {

    /**
     * Creates a sample {@link MapNode}.
     *
     * @return a sample {@link MapNode}
     */
    public static @NotNull MapNode mapNode() {
        var mapNode = MapNode.create();

        mapNode.set("string", "value");
        mapNode.set("integer", 100);
        mapNode.set("double", 3.14);
        mapNode.set("bool", true);
        mapNode.set("list", List.of("A", "B", "C"));
        mapNode.set("map", Map.of("key", "value"));
        mapNode.set("nested", Map.of("map", Map.of("key", "value")));

        return mapNode;
    }

    private Samples() {
        throw new UnsupportedOperationException();
    }
}
