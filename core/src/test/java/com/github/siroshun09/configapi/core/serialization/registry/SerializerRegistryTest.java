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

package com.github.siroshun09.configapi.core.serialization.registry;

import com.github.siroshun09.configapi.core.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class SerializerRegistryTest {

    static final Serializer<Integer, String> INT_SERIALIZER = value -> Integer.toString(value);
    static final Serializer<Long, String> LONG_SERIALIZER = value -> Long.toString(value);

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testRegisterAndGet(@NotNull SerializerRegistry<String> registry) {
        Assertions.assertSame(registry, registry.register(Integer.class, INT_SERIALIZER));
        Assertions.assertSame(INT_SERIALIZER, registry.get(Integer.class));
        Assertions.assertSame(INT_SERIALIZER, registry.getAsOptional(Integer.class).orElseThrow());
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertTrue(registry.getAsOptional(Long.class).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testFreeze(@NotNull SerializerRegistry<String> registry) {
        registry.register(Integer.class, INT_SERIALIZER);

        Assertions.assertFalse(registry.isFrozen());
        Assertions.assertSame(registry, registry.freeze());
        Assertions.assertTrue(registry.isFrozen());

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_SERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_SERIALIZER));

        Assertions.assertSame(INT_SERIALIZER, registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertDoesNotThrow(registry::freeze);
    }

    @Test
    void testEmpty() {
        var registry = SerializerRegistry.<String>empty();

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_SERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_SERIALIZER));

        Assertions.assertNull(registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));

        Assertions.assertTrue(registry.isFrozen());
        Assertions.assertSame(registry, registry.freeze());
        Assertions.assertTrue(registry.isFrozen());
    }

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testRegisterAll(@NotNull SerializerRegistry<String> registry) {
        Assertions.assertSame(registry, registry.registerAll(
                SerializerRegistry.<String>create()
                        .register(Integer.class, INT_SERIALIZER)
                        .register(Long.class, LONG_SERIALIZER)
                        .freeze()
        ));

        Assertions.assertSame(INT_SERIALIZER, registry.get(Integer.class));
        Assertions.assertSame(LONG_SERIALIZER, registry.get(Long.class));

        Assertions.assertDoesNotThrow(() -> registry.registerAll(SerializerRegistry.empty()));
    }

    private static @NotNull Stream<SerializerRegistry<String>> createRegistry() {
        return Stream.of(SerializerRegistry.create());
    }
}
