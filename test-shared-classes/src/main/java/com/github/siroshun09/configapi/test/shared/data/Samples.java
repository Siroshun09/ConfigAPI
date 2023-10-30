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

package com.github.siroshun09.configapi.test.shared.data;

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultBoolean;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultDouble;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultInt;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultString;
import com.github.siroshun09.configapi.core.serialization.annotation.MapType;
import com.github.siroshun09.configapi.core.serialization.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Samples {

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

    public enum Enum {
        A, B, C
    }

    public static @NotNull Record record() {
        return new Record("value", 100, 3.14, true, List.of("A", "B", "C"),
                Map.of("key", "value"), new NestedRecord(Map.of("key", "value")));
    }

    public record Record(@DefaultString("value") String string,
                         @DefaultInt(100) int integer,
                         @Key("double") @DefaultDouble(3.14) double doubleValue,
                         @DefaultBoolean(true) boolean bool,
                         @CollectionType(String.class) List<String> list,
                         @MapType(key = String.class, value = String.class) Map<String, String> map,
                         NestedRecord nested) {
    }

    public record NestedRecord(@MapType(key = String.class, value = String.class) Map<String, String> map) {
    }

    public static final UUID UUID_1 = UUID.randomUUID();
    public static final UUID UUID_2 = UUID.randomUUID();

    public static @NotNull UUIDRecord uuidRecord() {
        return new UUIDRecord(
                UUID_1,
                List.of(UUID_1, UUID_2),
                Map.of(UUID_1, 1, UUID_2, 2)
        );
    }

    public static @NotNull MapNode uuidRecordMapNode() {
        var mapNode = MapNode.create();
        mapNode.set("uuid", Samples.UUID_1.toString());
        mapNode.set("uuid-list", List.of(Samples.UUID_1.toString(), Samples.UUID_2.toString()));
        mapNode.set("uuid-int-map", Map.of(Samples.UUID_1.toString(), 1, Samples.UUID_2.toString(), 2));
        return mapNode;
    }

    public record UUIDRecord(UUID uuid,
                             @CollectionType(UUID.class) List<UUID> uuidList,
                             @MapType(key = UUID.class, value = Integer.class) Map<UUID, Integer> uuidIntMap
    ) {
    }

    private Samples() {
        throw new UnsupportedOperationException();
    }
}
