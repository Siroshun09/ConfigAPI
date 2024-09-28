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

package dev.siroshun.configapi.core.node;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapNodeTest extends AbstractCommentableNodeTest<MapNode> {

    @Override
    protected Stream<NodeTestCase<MapNode>> nodeTestCases() {
        return Stream.of(
                nodeTest("MapNode#create()", MapNode.create(), node -> {
                    assertTrue(node.isEmpty());
                    assertTrue(node.value().isEmpty());
                    assertTrue(node.hasValue()); // MapNode#hasValue always returns true
                    assertDoesNotThrow(() -> node.set("a", "b"));
                    assertDoesNotThrow(() -> node.setComment(COMMENT));
                }),
                nodeTest("MapNode#create(Map)", MapNode.create(Map.of("a", "b")), node -> {
                    assertFalse(node.isEmpty());
                    assertEquals(1, node.size());
                    assertFalse(node.value().isEmpty());
                    assertEquals(1, node.value().size());
                    assertTrue(node.hasValue()); // MapNode#hasValue always returns true
                    assertDoesNotThrow(() -> node.set("c", "d"));
                    assertDoesNotThrow(() -> node.setComment(COMMENT));
                }),
                nodeTest("MapNode#empty()", MapNode.empty(), node -> {
                    assertTrue(node.isEmpty());
                    assertTrue(node.value().isEmpty());
                    assertTrue(node.hasValue()); // MapNode#hasValue always returns true
                    assertThrows(UnsupportedOperationException.class, () -> node.set("a", "b"));
                    assertThrows(UnsupportedOperationException.class, () -> node.setComment(COMMENT));
                }),
                nodeTest("MapNode#get(Object)", MapNode.create(Map.of("a", "b")), node -> {
                    assertEquals(StringValue.fromString("b"), node.get("a"));
                    assertEquals(NullNode.NULL, node.get("1"));
                }),
                nodeTest("MapNode#get(Object, Node)", MapNode.create(Map.of("a", "b")), node -> {
                    var c = StringValue.fromString("c");
                    assertEquals(StringValue.fromString("b"), node.getOrDefault("a", c));
                    assertEquals(c, node.getOrDefault("1", c));
                }),
                nodeTest("MapNode#set(Object, Object)", MapNode.create(Map.of("a", "b")), node -> {
                    assertEquals(StringValue.fromString("b"), node.set("a", "A"));
                    assertEquals(NullNode.NULL, node.set("1", "2"));
                    assertEquals(StringValue.fromString("A"), node.set("a", null));
                    assertEquals(StringValue.fromString("2"), node.set("1", NullNode.NULL));
                    assertEquals(NullNode.NULL, node.set("b", null));
                }),
                nodeTest("MapNode#setIfAbsent(Object, Object)", MapNode.create(Map.of("a", "b")), node -> {
                    assertEquals(StringValue.fromString("b"), node.setIfAbsent("a", "c"));
                    assertEquals(StringValue.fromString("b"), node.get("a"));
                    assertNull(node.setIfAbsent("1", "2"));
                }),
                nodeTest(
                        "MapNode#putAll(Map)",
                        MapNode.create(Map.of("a", "b")),
                        node -> {
                            node.putAll(Map.of("c", "d"));
                            assertEquals(StringValue.fromString("b"), node.get("a"));
                            assertEquals(StringValue.fromString("d"), node.get("c"));
                        }
                ),
                nodeTest(
                        "MapNode#putAll(Map) by itself",
                        MapNode.create(Map.of("a", "b")),
                        node -> assertDoesNotThrow(() -> node.putAll(node.value()))
                ),
                nodeTest(
                        "MapNode#putAll(MapNode)",
                        MapNode.create(Map.of("a", "b")),
                        node -> {
                            node.putAll(MapNode.create(Map.of("c", "d")));
                            assertEquals(StringValue.fromString("b"), node.get("a"));
                            assertEquals(StringValue.fromString("d"), node.get("c"));
                        }
                ),
                nodeTest(
                        "MapNode#putAll(MapNode) by itself",
                        MapNode.create(Map.of("a", "b")),
                        node -> assertDoesNotThrow(() -> node.putAll(node))
                ),
                nodeTest(
                        "MapNode#replace(Object, Object)",
                        MapNode.create(Map.of("a", "b", "1", "2")),
                        node -> {
                            assertEquals(StringValue.fromString("b"), node.replace("a", "A"));
                            assertEquals(StringValue.fromString("2"), node.replace("1", null));
                            assertEquals(NullNode.NULL, node.replace("unknown", "value"));
                            return node;
                        },
                        (initial, modified) -> {
                            assertEquals(StringValue.fromString("A"), modified.get("a"));
                            assertEquals(NullNode.NULL, modified.get("1"));
                            assertEquals(NullNode.NULL, modified.get("unknown"));
                        }
                ),
                nodeTest(
                        "MapNode#remove(Object)",
                        MapNode.create(Map.of("a", "b")),
                        node -> {
                            assertEquals(StringValue.fromString("b"), node.remove("a"));
                            assertEquals(NullNode.NULL, node.remove("unknown"));
                            return node;
                        },
                        (initial, modified) -> {
                            assertEquals(NullNode.NULL, modified.get("a"));
                            assertEquals(NullNode.NULL, modified.get("unknown"));
                        }
                ),
                nodeTest(
                        "MapNode#clear()",
                        MapNode.create(Map.of("a", "b", "1", "2")),
                        node -> {
                            node.clear();
                            return node;
                        },
                        (initial, modified) -> {
                            assertTrue(modified.isEmpty());
                            assertEquals(NullNode.NULL, modified.get("a"));
                            assertEquals(NullNode.NULL, modified.get("1"));
                        }
                ),
                nodeTest(
                        "MapNode#copy()",
                        MapNode.create(Map.of("a", "b")),
                        MapNode::copy,
                        (initial, copied) -> {
                            assertNotSame(initial, copied);

                            initial.set("a", "A");
                            copied.set("a", "1");
                            assertEquals(Map.of("a", StringValue.fromString("A")), initial.value());
                            assertEquals(Map.of("a", StringValue.fromString("1")), copied.value());

                            copied.setComment(COMMENT);
                            assertNull(initial.getCommentOrNull());
                            assertSame(COMMENT, copied.getComment());
                        }
                ),
                nodeTest(
                        "MapNode#asView()",
                        MapNode.create(Map.of("a", "b")),
                        MapNode::asView,
                        (initial, view) -> {
                            assertNotSame(initial, view);

                            initial.set("a", "A");
                            assertThrows(UnsupportedOperationException.class, () -> view.set("a", "1"));
                            assertEquals(Map.of("a", StringValue.fromString("A")), initial.value());
                            assertEquals(Map.of("a", StringValue.fromString("A")), view.value());

                            assertThrows(UnsupportedOperationException.class, () -> view.setComment(COMMENT));
                            assertNull(initial.getCommentOrNull());
                            assertNull(view.getCommentOrNull());

                            initial.setComment(COMMENT);
                            assertSame(COMMENT, initial.getComment());
                            assertSame(COMMENT, view.getComment());
                        }
                ),
                nodeTest(
                        "MapNode#getString(String)",
                        MapNode.create(Map.of("a", "b", 1, 2)),
                        node -> {
                            assertEquals("b", node.getString("a"));
                            assertEquals("2", node.getString(1));
                            assertEquals("", node.getString("unknown"));
                            assertEquals("default", node.getString("unknown", "default"));
                        }
                )
        );
    }

    @Override
    protected Stream<FromObjectTestCase<MapNode>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTest(new HashMap<>(), map -> {
                    assertTrue(map.isEmpty());
                    assertDoesNotThrow(() -> map.set("a", "b"));
                }),
                fromObjectTest(Map.of("a", "b"), map -> {
                    assertEquals(Map.of("a", StringValue.fromString("b")), map.value());
                    assertDoesNotThrow(() -> map.set("c", "d"));
                })
        );
    }

    @Override
    protected MapNode cast(Node<?> node) {
        return (MapNode) node;
    }

    @Override
    protected Stream<MapNode> commentableNodes() {
        return Stream.of(MapNode.create());
    }
}
