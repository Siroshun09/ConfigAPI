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

    @Test
    void testGettingInteger() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getInteger("test-1"));
        Assertions.assertEquals(-5, config.getInteger("test-2"));

        config.set("test-3", Long.MAX_VALUE);
        Assertions.assertNotEquals(Long.MAX_VALUE, config.getInteger("test-3"));

        config.set("test-min", Integer.MIN_VALUE);
        config.set("test-max", Integer.MAX_VALUE);

        Assertions.assertEquals(Integer.MIN_VALUE, config.getInteger("test-min"));
        Assertions.assertEquals(Integer.MAX_VALUE, config.getInteger("test-max"));

        Assertions.assertEquals(0, config.getInteger("empty"));
        Assertions.assertEquals(100, config.getInteger("empty", 100));
    }

    @Test
    void testGettingLong() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getLong("test-1"));
        Assertions.assertEquals(-5, config.getLong("test-2"));

        config.set("test-3", Double.MAX_VALUE);
        Assertions.assertNotEquals(Double.MAX_VALUE, config.getLong("test-3"));

        config.set("test-min", Long.MIN_VALUE);
        config.set("test-max", Long.MAX_VALUE);

        Assertions.assertEquals(Long.MIN_VALUE, config.getLong("test-min"));
        Assertions.assertEquals(Long.MAX_VALUE, config.getLong("test-max"));

        Assertions.assertEquals(0, config.getLong("empty"));
        Assertions.assertEquals(100, config.getLong("empty", 100));
    }

    @Test
    void testGettingShort() {
        var config = newConfiguration();

        config.set("test-1", 5);
        config.set("test-2", -5);

        Assertions.assertEquals(5, config.getShort("test-1"));
        Assertions.assertEquals(-5, config.getShort("test-2"));

        config.set("test-3", Double.MAX_VALUE);
        Assertions.assertNotEquals(Double.MAX_VALUE, config.getShort("test-3"));

        config.set("test-min", Short.MIN_VALUE);
        config.set("test-max", Short.MAX_VALUE);

        Assertions.assertEquals(Short.MIN_VALUE, config.getShort("test-min"));
        Assertions.assertEquals(Short.MAX_VALUE, config.getShort("test-max"));

        Assertions.assertEquals(0, config.getShort("empty"));
        Assertions.assertEquals(100, config.getShort("empty", (short) 100));
    }

    @Test
    void testGettingString() {
        var config = newConfiguration();

        config.set("test-1", "test");
        Assertions.assertEquals("test", config.getString("test-1"));

        config.set("test-2", 100);
        Assertions.assertEquals("100", config.getString("test-2"));

        Assertions.assertEquals("", config.getString("empty"));
        Assertions.assertEquals("100", config.getString("empty", "100"));
    }

    @Test
    void testGettingBooleanTest() {
        var list = List.of(new Object(), true, false, Boolean.TRUE, Boolean.FALSE, 100, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getBooleanList("test");

        Assertions.assertEquals(4, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Boolean.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getBooleanList("empty"));

        var def = List.of(true, false, true, false);
        Assertions.assertSame(def, config.getBooleanList("empty", def));
    }

    @Test
    void testGettingByteList() {
        var list = List.of(new Object(), 5, -5, 300, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getByteList("test");

        Assertions.assertEquals(3, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Byte.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getByteList("empty"));

        var def = List.of((byte) 5, (byte) -5);
        Assertions.assertSame(def, config.getByteList("empty", def));
    }

    @Test
    void testGettingDoubleList() {
        var list = List.of(new Object(), 5.5, -5.5, 300, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getDoubleList("test");

        Assertions.assertEquals(3, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Double.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getDoubleList("empty"));

        var def = List.of(5.5, -5.5);
        Assertions.assertSame(def, config.getDoubleList("empty", def));
    }

    @Test
    void testGettingFloatList() {
        var list = List.of(new Object(), 5.5, -5.5, 300, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getFloatList("test");

        Assertions.assertEquals(4, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Float.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getFloatList("empty"));

        var def = List.of(5.5f, -5.5f);
        Assertions.assertSame(def, config.getFloatList("empty", def));
    }

    @Test
    void testGettingIntegerList() {
        var list = List.of(new Object(), 5, -5, Long.MAX_VALUE, 5.5, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getIntegerList("test");

        Assertions.assertEquals(4, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Integer.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getIntegerList("empty"));

        var def = List.of(5, -5);
        Assertions.assertSame(def, config.getIntegerList("empty", def));
    }

    @Test
    void testGettingLongList() {
        var list = List.of(new Object(), 5, -5, Long.MAX_VALUE, 5.5, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getLongList("test");

        Assertions.assertEquals(4, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Long.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getLongList("empty"));

        var def = List.of(5L, -5L);
        Assertions.assertSame(def, config.getLongList("empty", def));
    }

    @Test
    void testGettingShortList() {
        var list = List.of(new Object(), 5, -5, Long.MAX_VALUE, 5.5, true, "test");

        var config = newConfiguration();
        config.set("test", list);

        var actual = config.getShortList("test");

        Assertions.assertEquals(4, actual.size());

        for (Object object : actual) {
            Assertions.assertEquals(Short.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getShortList("empty"));

        var def = List.of((short) 5, (short) -5);
        Assertions.assertSame(def, config.getShortList("empty", def));
    }

    @Test
    void testGettingStringList() {
        var list = List.of(new Object(), "test", "", 100, true);

        var config = newConfiguration();
        config.set("test-1", list);

        var actual1 = config.getStringList("test-1");

        Assertions.assertEquals(5, actual1.size());

        for (Object object : actual1) {
            Assertions.assertEquals(String.class, object.getClass());
        }

        var includeNull = new ArrayList<>();
        includeNull.add("test");
        includeNull.add(null);
        includeNull.add(100);
        includeNull.add(true);

        config.set("test-2", includeNull);

        var actual2 = config.getStringList("test-2");

        Assertions.assertEquals(3, actual2.size());

        for (Object object : actual2) {
            Assertions.assertEquals(String.class, object.getClass());
        }

        Assertions.assertSame(Collections.emptyList(), config.getShortList("empty"));

        var def = List.of("test", "test");
        Assertions.assertSame(def, config.getStringList("empty", def));
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
