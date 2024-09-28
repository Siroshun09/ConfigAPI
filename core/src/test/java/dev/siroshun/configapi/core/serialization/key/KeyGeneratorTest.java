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

package dev.siroshun.configapi.core.serialization.key;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class KeyGeneratorTest {

    @Test
    void testCamelToKebab() {
        Map.of(
                "testTest", "test-test",
                "tTEST1", "t-test-1",
                "tEsT123", "t-es-t-123",
                "tTE1ST", "t-te-1-st",
                "tTEsT", "t-t-es-t"
        ).forEach((camel, kebab) -> Assertions.assertEquals(kebab, KeyGenerator.CAMEL_TO_KEBAB.generate(camel)));
    }

    @Test
    void testCamelToSnake() {
        Map.of(
                "testTest", "test_test",
                "tTEST1", "t_test_1",
                "tEsT123", "t_es_t_123",
                "tTE1ST", "t_te_1_st",
                "tTEsT", "t_t_es_t"
        ).forEach((camel, snake) -> Assertions.assertEquals(snake, KeyGenerator.CAMEL_TO_SNAKE.generate(camel)));
    }

    @ParameterizedTest
    @MethodSource("generators")
    void testNullOrEmpty(KeyGenerator generator) {
        Assertions.assertEquals("", generator.generate(""));
        Assertions.assertEquals("", generator.generate(null));
    }

    private static Stream<KeyGenerator> generators() {
        return Stream.of(
                KeyGenerator.AS_IS,
                KeyGenerator.CAMEL_TO_KEBAB,
                KeyGenerator.CAMEL_TO_SNAKE
        );
    }
}
