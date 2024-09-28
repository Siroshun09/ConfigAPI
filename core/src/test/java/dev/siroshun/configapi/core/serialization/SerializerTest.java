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

package dev.siroshun.configapi.core.serialization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SerializerTest {

    static final Serializer<Integer, String> INT_SERIALIZER = value -> Integer.toString(value);

    @Test
    void testApply() {
        var serializeResult = INT_SERIALIZER.serialize(100);
        var applyResult = INT_SERIALIZER.apply(100);
        Assertions.assertEquals(serializeResult, applyResult);
    }

    @Test
    void testCompose() {
        Serializer<List<?>, String> sizeSerializer = INT_SERIALIZER.compose(List::size);
        Assertions.assertEquals("3", sizeSerializer.serialize(List.of("a", "b", "c")));
    }

    @Test
    void testAndThen() {
        var lengthSerializer = INT_SERIALIZER.andThen(String::length);
        Assertions.assertEquals(3, lengthSerializer.serialize(100));
    }
}
