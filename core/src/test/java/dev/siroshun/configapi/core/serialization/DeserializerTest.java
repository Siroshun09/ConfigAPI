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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

class DeserializerTest {

    static final Deserializer<String, Integer> INT_DESERIALIZER = Integer::parseInt;

    @Test
    void testApply() {
        var serializeResult = INT_DESERIALIZER.deserialize("100");
        var applyResult = INT_DESERIALIZER.deserialize("100");
        Assertions.assertEquals(serializeResult, applyResult);
    }

    @Test
    void testCompose() {
        Deserializer<byte[], Integer> byteStrToInt = INT_DESERIALIZER.compose(bytes -> new String(bytes, StandardCharsets.UTF_8));
        Assertions.assertEquals(100, byteStrToInt.deserialize("100".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void testAndThen() {
        Deserializer<String, AtomicInteger> atomicIntegerDeserializer = INT_DESERIALIZER.andThen(AtomicInteger::new);
        Assertions.assertEquals(100, atomicIntegerDeserializer.deserialize("100").get());
    }
}
