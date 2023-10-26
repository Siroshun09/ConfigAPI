/*
 *     Copyright 2023 Siroshun09
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

package com.github.siroshun09.configapi.core.node;

import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MapNodeTest {

    @Test
    void testCreate() {
        var mapNode = MapNode.create();
        Assertions.assertTrue(mapNode.value().isEmpty());

        Assertions.assertDoesNotThrow(() -> mapNode.set(1, 1)); // checks if the list is modifiable
    }

    @Test
    void testCreateWithMap() {
        var original = new HashMap<>(Map.of("a", "b", "c", "d"));
        var mapNode = MapNode.create(original);

        Assertions.assertEquals(2, mapNode.value().size());

        // Checks if the MapNode is not modified by adding the entry to the original map.
        original.put("e", "f");
        Assertions.assertEquals(2, mapNode.value().size());

        // checks if the list is modifiable
        Assertions.assertDoesNotThrow(() -> mapNode.set("e", "f"));
        Assertions.assertDoesNotThrow(() -> MapNode.create(Map.of(1, 2)).set(3, 4));
    }

    @Test
    void testEmpty() {
        Assertions.assertTrue(MapNode.empty().value().isEmpty());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> MapNode.empty().set(1, 2));
    }

    @Test
    void testValue() {
        var mapNode = MapNode.create(Map.of("a", "b", 1, 2));
        Assertions.assertEquals(Map.of("a", new StringValue("b"), 1, new IntValue(2)), mapNode.value());
    }

    @Test
    void testHasValue() {
        Assertions.assertTrue(MapNode.create().hasValue());
        Assertions.assertTrue(MapNode.create(Map.of("a", "b", 1, 2)).hasValue());
        Assertions.assertTrue(MapNode.empty().hasValue());
    }

    @Test
    void testGet() {
        var mapNode = MapNode.create(Map.of("a", "b", 1, 2));
        Assertions.assertEquals(new StringValue("b"), mapNode.get("a"));
        Assertions.assertEquals(new IntValue(2), mapNode.get(1));
        Assertions.assertSame(NullNode.NULL, mapNode.get("b"));
        Assertions.assertSame(NullNode.NULL, MapNode.empty().get("a"));
    }

    @Test
    void testSet() {
        var mapNode = MapNode.create(Map.of("a", "b", 1, 2));

        Assertions.assertEquals(new StringValue("b"), mapNode.set("a", "c"));
        Assertions.assertEquals(new StringValue("c"), mapNode.get("a"));

        Assertions.assertEquals(new IntValue(2), mapNode.set(1, null));
        Assertions.assertEquals(NullNode.NULL, mapNode.get(1));

        Assertions.assertSame(NullNode.NULL, mapNode.set("d", "e"));
        Assertions.assertEquals(new StringValue("e"), mapNode.get("d"));

        Assertions.assertEquals(new StringValue("e"), mapNode.set("d", NullNode.NULL));
        Assertions.assertEquals(NullNode.NULL, mapNode.get("d"));

        Assertions.assertEquals(Map.of("a", new StringValue("c")), mapNode.value());
    }

    @Test
    void testClear() {
        var mapNode = MapNode.create(Map.of("a", "b", 1, 2));

        mapNode.clear();

        Assertions.assertTrue(mapNode.value().isEmpty());
        Assertions.assertSame(NullNode.NULL, mapNode.get("a"));
        Assertions.assertSame(NullNode.NULL, mapNode.get(1));
    }

    @Test
    void testCopy() {
        var mapNode = MapNode.create(Map.of("a", "b"));
        var copied = mapNode.copy();

        mapNode.set("c", 3);
        copied.set(3, "c");

        Assertions.assertEquals(Map.of("a", new StringValue("b"), "c", new IntValue(3)), mapNode.value());
        Assertions.assertEquals(Map.of("a", new StringValue("b"), 3, new StringValue("c")), copied.value());
    }

    @Test
    void testAsView() {
        var mapNode = MapNode.create(Map.of("a", "b"));
        var view = mapNode.asView();

        mapNode.set("c", "d");

        Assertions.assertEquals(Map.of("a", new StringValue("b"), "c", new StringValue("d")), mapNode.value());
        Assertions.assertEquals(Map.of("a", new StringValue("b"), "c", new StringValue("d")), view.value());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.set("e", "f"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.set("c", "f"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.set("a", null));

        Assertions.assertEquals(Map.of("a", new StringValue("b"), "c", new StringValue("d")), mapNode.value());
        Assertions.assertEquals(Map.of("a", new StringValue("b"), "c", new StringValue("d")), view.value());
    }

    @Test
    void testList() {
        var mapNode = Samples.mapNode();

        NodeAssertion.assertEquals(ListNode.create(List.of("A", "B", "C")), mapNode.getList("list"));
        Assertions.assertSame(ListNode.empty(), mapNode.getList("string"));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> mapNode.getList("list").add("D"));

        NodeAssertion.assertEquals(ListNode.create(List.of("A", "B", "C")), mapNode.getOrCreateList("list"));

        var list = mapNode.getOrCreateList("string");
        Assertions.assertNotSame(ListNode.empty(), mapNode.getList("string"));
        list.add("A");
        NodeAssertion.assertEquals(ListNode.create(List.of("A")), mapNode.getList("string"));

        var newList = mapNode.createList("string");
        newList.add("B");
        NodeAssertion.assertEquals(ListNode.create(List.of("B")), mapNode.getList("string"));
    }

    @Test
    void testMap() {
        var mapNode = Samples.mapNode();

        NodeAssertion.assertEquals(MapNode.create(Map.of("key", "value")), mapNode.getMap("map"));
        Assertions.assertSame(MapNode.empty(), mapNode.getMap("string"));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> mapNode.getMap("map").set("a", "b"));

        NodeAssertion.assertEquals(MapNode.create(Map.of("key", "value")), mapNode.getOrCreateMap("map"));

        var map = mapNode.getOrCreateMap("string");
        Assertions.assertNotSame(MapNode.empty(), mapNode.getMap("string"));
        map.set("A", "B");
        NodeAssertion.assertEquals(MapNode.create(Map.of("A", "B")), mapNode.getMap("string"));

        var newMap = mapNode.createMap("string");
        newMap.set("C", "D");
        NodeAssertion.assertEquals(MapNode.create(Map.of("C", "D")), mapNode.getMap("string"));
    }

    @Test
    void testString() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals("value", mapNode.getString("string"));
        Assertions.assertEquals("", mapNode.getString("integer"));

        Assertions.assertEquals("value", mapNode.getString("string", "default"));
        Assertions.assertEquals("default", mapNode.getString("integer", "default"));

        Assertions.assertEquals("value", mapNode.getStringOrNull("string"));
        Assertions.assertNull(mapNode.getStringOrNull("integer"));
    }

    @Test
    void testEnum() {
        var mapNode = Samples.mapNode();

        mapNode.set("enum", Samples.Enum.A);
        mapNode.set("enum-name", "B");
        mapNode.set("enum-lower-name", "b");

        Assertions.assertEquals(Samples.Enum.A, mapNode.getEnum("enum", Samples.Enum.class));
        Assertions.assertEquals(Samples.Enum.B, mapNode.getEnum("enum-name", Samples.Enum.class));
        Assertions.assertNull(mapNode.getEnum("string", Samples.Enum.class));
        Assertions.assertNull(mapNode.getEnum("integer", Samples.Enum.class));

        Assertions.assertEquals(Samples.Enum.A, mapNode.getEnum("enum", Samples.Enum.C));
        Assertions.assertEquals(Samples.Enum.B, mapNode.getEnum("enum-name", Samples.Enum.C));
        Assertions.assertEquals(Samples.Enum.C, mapNode.getEnum("string", Samples.Enum.C));
        Assertions.assertEquals(Samples.Enum.C, mapNode.getEnum("integer", Samples.Enum.C));

        Assertions.assertEquals(Samples.Enum.B, mapNode.getEnum("enum-lower-name", Samples.Enum.C));
    }

    @Test
    void testBoolean() {
        var mapNode = Samples.mapNode();

        Assertions.assertTrue(mapNode.getBoolean("bool"));
        Assertions.assertFalse(mapNode.getBoolean("string"));

        Assertions.assertTrue(mapNode.getBoolean("bool"));
        Assertions.assertTrue(mapNode.getBoolean("string", true));
    }

    @Test
    void testInteger() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100, mapNode.getInteger("integer"));
        Assertions.assertEquals(3, mapNode.getInteger("double"));
        Assertions.assertEquals(0, mapNode.getInteger("string"));
        Assertions.assertEquals(10, mapNode.getInteger("string", 10));
    }

    @Test
    void testLong() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100, mapNode.getLong("integer"));
        Assertions.assertEquals(3, mapNode.getLong("double"));
        Assertions.assertEquals(0, mapNode.getLong("string"));
        Assertions.assertEquals(10, mapNode.getLong("string", 10));
    }

    @Test
    void testFloat() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100f, mapNode.getFloat("integer"));
        Assertions.assertEquals(3.14f, mapNode.getFloat("double"));
        Assertions.assertEquals(0, mapNode.getFloat("string"));
        Assertions.assertEquals(10.5f, mapNode.getFloat("string", 10.5f));
    }

    @Test
    void testDouble() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100, mapNode.getDouble("integer"));
        Assertions.assertEquals(3.14, mapNode.getDouble("double"));
        Assertions.assertEquals(0, mapNode.getDouble("string"));
        Assertions.assertEquals(10.5, mapNode.getDouble("string", 10.5f));
    }

    @Test
    void testByte() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100, mapNode.getByte("integer"));
        Assertions.assertEquals(3, mapNode.getByte("double"));
        Assertions.assertEquals(0, mapNode.getByte("string"));
        Assertions.assertEquals(10, mapNode.getByte("string", (byte) 10));
    }

    @Test
    void testShort() {
        var mapNode = Samples.mapNode();

        Assertions.assertEquals(100, mapNode.getShort("integer"));
        Assertions.assertEquals(3, mapNode.getShort("double"));
        Assertions.assertEquals(0, mapNode.getShort("string"));
        Assertions.assertEquals(10, mapNode.getShort("string", (short) 10));
    }
}
