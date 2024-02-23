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

package com.github.siroshun09.configapi.core.serialization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializationTest {

    @Test
    void testCreate() {
        var serialization = Serialization.create(SerializerTest.INT_SERIALIZER, DeserializerTest.INT_DESERIALIZER);

        Assertions.assertTrue(serialization.hasSerializer());
        Assertions.assertSame(SerializerTest.INT_SERIALIZER, serialization.serializer());
        Assertions.assertTrue(serialization.hasDeserializer());
        Assertions.assertSame(DeserializerTest.INT_DESERIALIZER, serialization.deserializer());
    }

    @Test
    void testOnlySerializer() {
        var serialization = Serialization.onlySerializer(SerializerTest.INT_SERIALIZER);

        Assertions.assertTrue(serialization.hasSerializer());
        Assertions.assertSame(SerializerTest.INT_SERIALIZER, serialization.serializer());
        Assertions.assertFalse(serialization.hasDeserializer());
        Assertions.assertThrows(IllegalStateException.class, serialization::deserializer);
    }

    @Test
    void testOnlyDeserializer() {
        var serialization = Serialization.onlyDeserializer(DeserializerTest.INT_DESERIALIZER);

        Assertions.assertFalse(serialization.hasSerializer());
        Assertions.assertThrows(IllegalStateException.class, serialization::serializer);
        Assertions.assertTrue(serialization.hasDeserializer());
        Assertions.assertSame(DeserializerTest.INT_DESERIALIZER, serialization.deserializer());
    }
}
