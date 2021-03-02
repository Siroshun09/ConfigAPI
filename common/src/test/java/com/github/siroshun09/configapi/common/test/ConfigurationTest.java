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

package com.github.siroshun09.configapi.common.test;

import com.github.siroshun09.configapi.common.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigurationTest {

    @Test
    void testSetAndGetValue() {
        Configuration config = Configuration.create();

        config.set("key1", "value1");
        config.set("aaa.bbb.ccc", "value2");
        config.set("ddd.eee.fff", 1);
        config.set("ddd.eee.fff", 5.0);

        Assertions.assertEquals("value1", config.get("key1"));
        Assertions.assertEquals("value2", config.get("aaa.bbb.ccc"));
        Assertions.assertEquals(5.0, config.get("ddd.eee.fff"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get("ddd."));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.get(".ddd"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("ddd.", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.set("aaa..ccc", null));
    }
}
