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

import com.github.siroshun09.configapi.core.serialization.Serialization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializationRegistryTest {

    private static final SerializerRegistryTest SHARED_SERIALIZER_REGISTRY_TEST = new SerializerRegistryTest();
    private static final DeserializerRegistryTest SHARED_DESERIALIZER_REGISTRY_TEST = new DeserializerRegistryTest();

    static final Serialization<Integer, String> INT_SERIALIZATION = Serialization.create(SerializerRegistryTest.INT_SERIALIZER, DeserializerRegistryTest.INT_DESERIALIZER);
    static final Serialization<Long, String> LONG_SERIALIZATION = Serialization.create(SerializerRegistryTest.LONG_SERIALIZER, DeserializerRegistryTest.LONG_DESERIALIZER);

    @Test
    void testRegisterAndGet() {
        var registry = SerializationRegistry.create();
        Assertions.assertSame(registry, registry.register(Integer.class, INT_SERIALIZATION));
        Assertions.assertSame(INT_SERIALIZATION, registry.get(Integer.class));
        Assertions.assertSame(INT_SERIALIZATION, registry.getAsOptional(Integer.class).orElseThrow());
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertTrue(registry.getAsOptional(Long.class).isEmpty());
    }

    @Test
    void testFreeze() {
        var registry = SerializationRegistry.<String>create().register(Integer.class, INT_SERIALIZATION);

        Assertions.assertFalse(registry.isFrozen());
        Assertions.assertSame(registry, registry.freeze());
        Assertions.assertTrue(registry.isFrozen());

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_SERIALIZATION));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_SERIALIZATION));

        Assertions.assertSame(INT_SERIALIZATION, registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));
        Assertions.assertDoesNotThrow(registry::freeze);

        Assertions.assertTrue(registry.asSerializerRegistry().isFrozen());
        Assertions.assertThrows(IllegalStateException.class, () -> registry.asSerializerRegistry().register(Integer.class, SerializerRegistryTest.INT_SERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.asSerializerRegistry().register(Long.class, SerializerRegistryTest.LONG_SERIALIZER));

        Assertions.assertTrue(registry.asDeserializerRegistry().isFrozen());
        Assertions.assertThrows(IllegalStateException.class, () -> registry.asDeserializerRegistry().register(Integer.class, DeserializerRegistryTest.INT_DESERIALIZER));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.asDeserializerRegistry().register(Long.class, DeserializerRegistryTest.LONG_DESERIALIZER));
    }

    @Test
    void testEmpty() {
        var registry = SerializationRegistry.<String>empty();

        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Integer.class, INT_SERIALIZATION));
        Assertions.assertThrows(IllegalStateException.class, () -> registry.register(Long.class, LONG_SERIALIZATION));

        Assertions.assertNull(registry.get(Integer.class));
        Assertions.assertNull(registry.get(Long.class));
    }

    @Test
    void testRegisterAll() {
        var registry = SerializationRegistry.<String>create();
        Assertions.assertSame(registry, registry.registerAll(
                SerializationRegistry.<String>create()
                        .register(Integer.class, INT_SERIALIZATION)
                        .register(Long.class, LONG_SERIALIZATION)
                        .freeze()
        ));

        Assertions.assertSame(INT_SERIALIZATION, registry.get(Integer.class));
        Assertions.assertSame(LONG_SERIALIZATION, registry.get(Long.class));
    }

    @Test
    void testAsSerializerRegistry() {
        var r1 = SerializationRegistry.<String>create();
        SHARED_SERIALIZER_REGISTRY_TEST.testRegisterAndGet(r1.asSerializerRegistry());
        var s1 = r1.get(Integer.class);
        Assertions.assertNotNull(s1);
        Assertions.assertSame(SerializerRegistryTest.INT_SERIALIZER, s1.serializer());
        Assertions.assertNull(r1.get(Long.class));

        var r2 = SerializationRegistry.<String>create();
        SHARED_SERIALIZER_REGISTRY_TEST.testFreeze(r2.asSerializerRegistry());
        Assertions.assertTrue(r2.asSerializerRegistry().isFrozen());
        Assertions.assertTrue(r2.isFrozen());
        var s2 = r2.get(Integer.class);
        Assertions.assertNotNull(s2);
        Assertions.assertSame(SerializerRegistryTest.INT_SERIALIZER, s2.serializer());
        Assertions.assertNull(r2.get(Long.class));
        Assertions.assertThrows(IllegalStateException.class, () -> r2.register(Integer.class, INT_SERIALIZATION));
        Assertions.assertThrows(IllegalStateException.class, () -> r2.register(Long.class, LONG_SERIALIZATION));

        var r3 = SerializationRegistry.<String>create();
        SHARED_SERIALIZER_REGISTRY_TEST.testRegisterAll(r3.asSerializerRegistry());
        Assertions.assertSame(SerializerRegistryTest.INT_SERIALIZER, r3.getAsOptional(Integer.class).map(Serialization::serializer).orElseThrow());
        Assertions.assertSame(SerializerRegistryTest.LONG_SERIALIZER, r3.getAsOptional(Long.class).map(Serialization::serializer).orElseThrow());

        var r4 = SerializationRegistry.<String>create();
        r4.register(Integer.class, INT_SERIALIZATION);
        SHARED_SERIALIZER_REGISTRY_TEST.testRegisterAndGet(r4.asSerializerRegistry());
        var s4 = r4.get(Integer.class);
        Assertions.assertNotNull(s4);
        Assertions.assertSame(SerializerRegistryTest.INT_SERIALIZER, s4.serializer());
        Assertions.assertSame(INT_SERIALIZATION.deserializer(), s4.deserializer());
    }

    @Test
    void testAsDeserializerRegistry() {
        var r1 = SerializationRegistry.<String>create();
        SHARED_DESERIALIZER_REGISTRY_TEST.testRegisterAndGet(r1.asDeserializerRegistry());
        var s1 = r1.get(Integer.class);
        Assertions.assertNotNull(s1);
        Assertions.assertSame(DeserializerRegistryTest.INT_DESERIALIZER, s1.deserializer());
        Assertions.assertNull(r1.get(Long.class));

        var r2 = SerializationRegistry.<String>create();
        SHARED_DESERIALIZER_REGISTRY_TEST.testFreeze(r2.asDeserializerRegistry());
        Assertions.assertTrue(r2.asDeserializerRegistry().isFrozen());
        Assertions.assertTrue(r2.isFrozen());
        var s2 = r2.get(Integer.class);
        Assertions.assertNotNull(s2);
        Assertions.assertSame(DeserializerRegistryTest.INT_DESERIALIZER, s2.deserializer());
        Assertions.assertNull(r2.get(Long.class));
        Assertions.assertThrows(IllegalStateException.class, () -> r2.register(Integer.class, INT_SERIALIZATION));
        Assertions.assertThrows(IllegalStateException.class, () -> r2.register(Long.class, LONG_SERIALIZATION));

        var r3 = SerializationRegistry.<String>create();
        SHARED_DESERIALIZER_REGISTRY_TEST.testRegisterAll(r3.asDeserializerRegistry());
        Assertions.assertSame(DeserializerRegistryTest.INT_DESERIALIZER, r3.getAsOptional(Integer.class).map(Serialization::deserializer).orElseThrow());
        Assertions.assertSame(DeserializerRegistryTest.LONG_DESERIALIZER, r3.getAsOptional(Long.class).map(Serialization::deserializer).orElseThrow());

        var r4 = SerializationRegistry.<String>create();
        r4.register(Integer.class, INT_SERIALIZATION);
        SHARED_DESERIALIZER_REGISTRY_TEST.testRegisterAndGet(r4.asDeserializerRegistry());
        var s4 = r4.get(Integer.class);
        Assertions.assertNotNull(s4);
        Assertions.assertSame(INT_SERIALIZATION.serializer(), s4.serializer());
        Assertions.assertSame(DeserializerRegistryTest.INT_DESERIALIZER, s4.deserializer());
    }
}
