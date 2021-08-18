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

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

public class AbstractConfigurationTest {

    @Test
    void testGettingDefaultValue() {
        var config = newConfiguration();

        var def = new Object();
        Assertions.assertSame(def, config.get("test", def));
    }

    @Test
    void testGettingList() {
        var config = newConfiguration();

        var list = new ArrayList<>();
        config.set("test-list", list);

        Assertions.assertSame(list, config.getList("test-list"));
        Assertions.assertSame(Collections.emptyList(), config.getList("test"));
    }

    @Test
    void testGettingBoolean() {
        var config = newConfiguration();

        config.set("test", true);
        Assertions.assertTrue(config.getBoolean("test"));

        config.set("test-string", "true");
        Assertions.assertFalse(config.getBoolean("test-string"));
        Assertions.assertTrue(config.getBoolean("test-string", true));
    }

    @Contract(" -> new")
    private static @NotNull Configuration newConfiguration() {
        return MappedConfiguration.create();
    }

}
