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

    /**
     * An enum that is used for {@link #mapNode()} and {@link Record}.
     */
    public enum Enum {
        /**
         * Sample enum value: A
         */
        A,
        /**
         * Sample enum value: B
         */
        B,
        /**
         * Sample enum value: C
         */
        C
    }

    /**
     * Creates a sample {@link Record}.
     *
     * @return a sample {@link Record}
     */
    public static @NotNull Record record() {
        return new Record("value", 100, 3.14, true, List.of("A", "B", "C"),
                Map.of("key", "value"), new NestedRecord(Map.of("key", "value")));
    }

    /**
     * A sample {@link java.lang.Record} class.
     *
     * @param string      the {@link String} field
     * @param integer     the int field
     * @param doubleValue the double field
     * @param bool        the boolean field
     * @param list        the {@link List} field
     * @param map         the {@link Map} field
     * @param nested      the {@link NestedRecord} field
     */
    public record Record(@DefaultString("value") String string,
                         @DefaultInt(100) int integer,
                         @Key("double") @DefaultDouble(3.14) double doubleValue,
                         @DefaultBoolean(true) boolean bool,
                         @CollectionType(String.class) List<String> list,
                         @MapType(key = String.class, value = String.class) Map<String, String> map,
                         NestedRecord nested) {
    }

    /**
     * A {@link java.lang.Record} that is used in sample {@link Record}.
     *
     * @param map the {@link Map} field
     */
    public record NestedRecord(@MapType(key = String.class, value = String.class) Map<String, String> map) {
    }

    /**
     * A first {@link UUID} that is used in {@link UUIDRecord}.
     */
    public static final UUID UUID_1 = UUID.randomUUID();

    /**
     * A second {@link UUID} that is used in {@link UUIDRecord}.
     */
    public static final UUID UUID_2 = UUID.randomUUID();

    /**
     * Creates a sample {@link UUIDRecord}.
     *
     * @return a sample {@link UUIDRecord}
     */
    public static @NotNull UUIDRecord uuidRecord() {
        return new UUIDRecord(
                UUID_1,
                List.of(UUID_1, UUID_2),
                Map.of(UUID_1, 1, UUID_2, 2)
        );
    }

    /**
     * Creates a {@link MapNode} that represents {@link #uuidRecord()} using {@link com.github.siroshun09.configapi.core.serialization.key.KeyGenerator#CAMEL_TO_KEBAB}.
     *
     * @return a {@link MapNode} that represents {@link #uuidRecord()}
     */
    public static @NotNull MapNode uuidRecordMapNode() {
        var mapNode = MapNode.create();
        mapNode.set("uuid", Samples.UUID_1.toString());
        mapNode.set("uuid-list", List.of(Samples.UUID_1.toString(), Samples.UUID_2.toString()));
        mapNode.set("uuid-int-map", Map.of(Samples.UUID_1.toString(), 1, Samples.UUID_2.toString(), 2));
        return mapNode;
    }

    /**
     * A {@link java.lang.Record} that holds {@link UUID}.
     *
     * @param uuid       the {@link UUID} field
     * @param uuidList   the {@link List} field
     * @param uuidIntMap the {@link Map} field
     */
    public record UUIDRecord(UUID uuid,
                             @CollectionType(UUID.class) List<UUID> uuidList,
                             @MapType(key = UUID.class, value = Integer.class) Map<UUID, Integer> uuidIntMap
    ) {
    }

    private Samples() {
        throw new UnsupportedOperationException();
    }
}
