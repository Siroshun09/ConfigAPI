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

package dev.siroshun.configapi.core.serialization.registry;

import dev.siroshun.configapi.core.serialization.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DeserializerRegistryTest {

    static final Deserializer<String, Integer> INT_DESERIALIZER = Integer::parseInt;
    static final Deserializer<String, Long> LONG_DESERIALIZER = Long::parseLong;

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testRegisterAndGet(@NotNull DeserializerRegistry<String> registry) {
        Assertions.assertSame(registry, registry.register(Integer.class, INT_DESERIALIZER));
        Assertions.assertSame(INT_DESERIALIZER, registry.get(Integer.class));
        Assertions.assertSame(INT_DESERIALIZER, registry.getAsOptional(Integer.class).orElseThrow());
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertTrue(registry.getAsOptional(Long.class).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testFreeze(@NotNull DeserializerRegistry<String> registry) {
        registry.register(Integer.class, INT_DESERIALIZER);

        Assertions.assertFalse(registry.isFrozen());
        Assertions.assertSame(registry, registry.freeze());
        Assertions.assertTrue(registry.isFrozen());

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_DESERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_DESERIALIZER));

        Assertions.assertSame(INT_DESERIALIZER, registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertDoesNotThrow(registry::freeze);
    }

    @Test
    void testEmpty() {
        var registry = DeserializerRegistry.<String>empty();

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_DESERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_DESERIALIZER));

        Assertions.assertNull(registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));

        Assertions.assertTrue(registry.isFrozen());
        Assertions.assertSame(registry, registry.freeze());
        Assertions.assertTrue(registry.isFrozen());
    }

    @ParameterizedTest
    @MethodSource("createRegistry")
    void testRegisterAll(@NotNull DeserializerRegistry<String> registry) {
        Assertions.assertSame(registry, registry.registerAll(
                DeserializerRegistry.<String>create()
                        .register(Integer.class, INT_DESERIALIZER)
                        .register(Long.class, LONG_DESERIALIZER)
                        .freeze()
        ));

        Assertions.assertSame(INT_DESERIALIZER, registry.get(Integer.class));
        Assertions.assertSame(LONG_DESERIALIZER, registry.get(Long.class));

        Assertions.assertDoesNotThrow(() -> registry.registerAll(DeserializerRegistry.empty()));
    }

    private static @NotNull Stream<DeserializerRegistry<String>> createRegistry() {
        return Stream.of(DeserializerRegistry.create());
    }
}
