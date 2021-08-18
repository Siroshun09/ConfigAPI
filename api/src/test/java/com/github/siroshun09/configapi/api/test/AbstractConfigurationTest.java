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
import java.util.List;

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

    @Test
    void testGettingByte() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getByte("test-1"));
        Assertions.assertEquals(-5, config.getByte("test-2"));

        config.set("test-3", 300);
        Assertions.assertNotEquals(300, config.getByte("test-3"));

        config.set("test-min", Byte.MIN_VALUE);
        config.set("test-max", Byte.MAX_VALUE);

        Assertions.assertEquals(Byte.MIN_VALUE, config.getByte("test-min"));
        Assertions.assertEquals(Byte.MAX_VALUE, config.getByte("test-max"));

        Assertions.assertEquals(0, config.getByte("empty"));
        Assertions.assertEquals(100, config.getByte("empty", (byte) 100));
    }

    @Test
    void testGettingDouble() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getDouble("test-1"));
        Assertions.assertEquals(-5, config.getDouble("test-2"));

        config.set("test-min", Double.MIN_VALUE);
        config.set("test-max", Double.MAX_VALUE);

        Assertions.assertEquals(Double.MIN_VALUE, config.getDouble("test-min"));
        Assertions.assertEquals(Double.MAX_VALUE, config.getDouble("test-max"));

        Assertions.assertEquals(0, config.getDouble("empty"));
        Assertions.assertEquals(100, config.getDouble("empty", 100));
    }

    @Test
    void testGettingFloat() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getFloat("test-1"));
        Assertions.assertEquals(-5, config.getFloat("test-2"));

        config.set("test-3", Double.MAX_VALUE);
        Assertions.assertNotEquals(Double.MAX_VALUE, config.getFloat("test-3"));

        config.set("test-min", Float.MIN_VALUE);
        config.set("test-max", Float.MAX_VALUE);

        Assertions.assertEquals(Float.MIN_VALUE, config.getFloat("test-min"));
        Assertions.assertEquals(Float.MAX_VALUE, config.getFloat("test-max"));

        Assertions.assertEquals(0, config.getFloat("empty"));
        Assertions.assertEquals(100, config.getFloat("empty", 100));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testIllegalArguments() {
        var config = newConfiguration();

        Assertions.assertTrue(testThrowingForIllegalArgument(() -> config.get("test", (Object) null)));
        Assertions.assertTrue(testThrowingForIllegalArgument(() -> config.getList(null)));
        Assertions.assertTrue(testThrowingForIllegalArgument(() -> config.getList("test", (List<?>) null)));
    }

    @Contract(" -> new")
    private static @NotNull Configuration newConfiguration() {
        return MappedConfiguration.create();
    }

    private static boolean testThrowingForIllegalArgument(@NotNull Runnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable throwable) {
            return throwable instanceof NullPointerException || throwable instanceof IllegalArgumentException;
        }
    }
}
