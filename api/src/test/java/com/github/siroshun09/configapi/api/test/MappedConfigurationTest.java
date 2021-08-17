/*
 *     Copyright 2021 Siroshun09
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

package com.github.siroshun09.configapi.api.test;

import com.github.siroshun09.configapi.api.AbstractConfiguration;
import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class MappedConfigurationTest {

    @Test
    void testMappedConfiguration() {
        var config = MappedConfiguration.create();

        config.set("key1", "value1");
        config.set("aaa.bbb.ccc", "value2");
        config.set("ddd.eee.fff", 1);
        config.set("ddd.eee.fff", 5.0);

        Assertions.assertEquals("value1", config.get("key1"));
        Assertions.assertEquals("value2", config.get("aaa.bbb.ccc"));
        Assertions.assertEquals(5.0, config.get("ddd.eee.fff"));

        var other = MappedConfiguration.create();
        other.set("key", "value");
        config.set("other-config", other);

        Assertions.assertEquals("value", config.get("other-config.key"));

        var section = config.getSection("other-config");
        Assertions.assertEquals(other, section);
    }

    @Test
    void testSettingConfiguration() {
        var config = MappedConfiguration.create();

        var source = new AbstractConfiguration() {
            @Override
            public @Nullable Object get(@NotNull String path) {
                switch (path) {
                    case "key":
                        return "value";
                    case "map":
                        var c = MappedConfiguration.create();
                        c.set("key", "value");
                        return c;
                    default:
                        return null;
                }
            }

            @Override
            public void set(@NotNull String path, @Nullable Object value) {
            }

            @Override
            public @NotNull @Unmodifiable List<String> getKeyList() {
                return List.of("key", "map");
            }

            @Override
            public @NotNull @Unmodifiable Set<Object> getValues() {
                return Collections.emptySet();
            }

            @Override
            public @Nullable Configuration getSection(@NotNull String path) {
                return null;
            }
        };

        config.set("other-config", source);

        Assertions.assertEquals("value", config.get("other-config.key"));
        Assertions.assertEquals("value", config.get("other-config.map.key"));
        Assertions.assertEquals(LinkedHashMap.class, Objects.requireNonNull(config.get("other-config.map")).getClass());
    }

    @Test
    void testInvalidPath() {
        var config = MappedConfiguration.create();
        //noinspection ConstantConditions
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get((String) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get("ddd."));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get(".ddd"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("ddd.", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("aaa..ccc", null));
    }
}
