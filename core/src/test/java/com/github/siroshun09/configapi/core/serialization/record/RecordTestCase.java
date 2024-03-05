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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.node.MapNode;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

record RecordTestCase<R extends Record>(@NotNull R expectedRecord, @NotNull Consumer<MapNode> expectedMapNodeBuilder) {

    static <R extends Record> @NotNull RecordTestCase<R> create(@NotNull R expectedRecord, @NotNull Consumer<MapNode> expectedMapNodeBuilder) {
        return new RecordTestCase<>(expectedRecord, expectedMapNodeBuilder);
    }

    @NotNull MapNode expectedMapNode() {
        var mapNode = MapNode.create();
        this.expectedMapNodeBuilder.accept(mapNode);
        return mapNode;
    }
}
