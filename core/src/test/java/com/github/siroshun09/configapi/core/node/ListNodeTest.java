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

package com.github.siroshun09.configapi.core.node;

import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListNodeTest extends AbstractCommentableNodeTest<ListNode> {

    @Override
    protected Stream<NodeTestCase<ListNode>> nodeTestCases() {
        return Stream.of(
                nodeTest("ListNode#create()", ListNode.create(), node -> {
                    assertTrue(node.isEmpty());
                    assertTrue(node.value().isEmpty());
                    assertTrue(node.hasValue()); // ListNode#hasValue always returns true
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                    assertDoesNotThrow(() -> node.setComment(COMMENT));
                }),
                nodeTest("ListNode#create(int)", ListNode.create(10), node -> {
                    assertTrue(node.isEmpty());
                    assertTrue(node.value().isEmpty());
                    assertTrue(node.hasValue()); // ListNode#hasValue always returns true
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                    assertDoesNotThrow(() -> node.setComment(COMMENT));
                }),
                nodeTest("ListNode#create(Collection)", ListNode.create(List.of(1, 2, 3)), node -> {
                    assertEquals(3, node.size());
                    assertEquals(List.of(new IntValue(1), new IntValue(2), new IntValue(3)), node.value());
                    assertTrue(node.hasValue()); // ListNode#hasValue always returns true
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                    assertDoesNotThrow(() -> node.setComment(COMMENT));
                }),
                nodeTest("ListNode#empty", ListNode.empty(), node -> {
                    assertTrue(node.isEmpty());
                    assertTrue(node.value().isEmpty());
                    assertTrue(node.hasValue()); // ListNode#hasValue always returns true
                    assertThrows(UnsupportedOperationException.class, () -> node.add(NullNode.NULL));
                    assertThrows(UnsupportedOperationException.class, () -> node.setComment(COMMENT));
                }),
                nodeTest("ListNode#asList(Class)", ListNode.create(List.of("a", 1, 3.14, true, new int[0])), node -> {
                    assertEquals(List.of(StringValue.fromString("a"), new IntValue(1), new DoubleValue(3.14), BooleanValue.TRUE, new IntArray(new int[0])), node.value());

                    assertEquals(List.of(StringValue.fromString("a")), node.asList(StringValue.class));

                    assertEquals(List.of(new IntValue(1)), node.asList(IntValue.class));
                    assertEquals(List.of(1), node.asList(Integer.class));

                    assertEquals(List.of(new IntValue(1), new DoubleValue(3.14)), node.asList(NumberValue.class));
                    assertEquals(List.of(1, 3.14), node.asList(Number.class));

                    // Special case "String": Returns the list that contains StringRepresentable#asString nodes
                    assertEquals(List.of("a", "1", "3.14", "true"), node.asList(String.class));
                }),
                nodeTest("ListNode#stream()", ListNode.create(List.of("a", 1, 3.14, true, new int[0])), node -> {
                    var expectedOrder = new ArrayList<>(List.of(StringValue.fromString("a"), new IntValue(1), new DoubleValue(3.14), BooleanValue.TRUE, new IntArray(new int[0])));
                    node.stream().forEach(element -> NodeAssertion.assertEquals(expectedOrder.remove(0), element));
                    assertTrue(expectedOrder.isEmpty());
                }),
                nodeTest(
                        "ListNode#add(Object)",
                        ListNode.create(),
                        node -> {
                            node.add("a");
                            node.add(1);
                            node.add(null);
                            return node;
                        },
                        (initial, modified) -> assertEquals(List.of(StringValue.fromString("a"), new IntValue(1), NullNode.NULL), modified.value())
                ),
                nodeTest(
                        "ListNode#addAll(Collection)",
                        ListNode.create(),
                        node -> {
                            node.addAll(List.of("a", 1, true));
                            return node;
                        },
                        (initial, modified) -> assertEquals(List.of(StringValue.fromString("a"), new IntValue(1), BooleanValue.TRUE), modified.value())
                ),
                nodeTest(
                        "ListNode#addAll(ListNode)",
                        ListNode.create(List.of("a", "b", "c")),
                        node -> {
                            var other = ListNode.create(List.of("d", "e", "f"));
                            node.addAll(other);
                            return node;
                        },
                        (initial, modified) -> {
                            assertEquals(6, modified.size());
                            assertEquals(List.of("a", "b", "c", "d", "e", "f"), modified.asList(String.class));
                        }
                ),
                nodeTest(
                        "ListNode#addAll(ListNode) by itself",
                        ListNode.create(List.of("a", "b", "c")),
                        node -> {
                            node.addAll(node);
                            return node;
                        },
                        (initial, modified) -> {
                            assertEquals(6, modified.size());
                            assertEquals(List.of("a", "b", "c", "a", "b", "c"), modified.asList(String.class));
                        }
                ),
                nodeTest(
                        "ListNode#addList()",
                        ListNode.create(),
                        ListNode::addList,
                        (initial, added) -> {
                            assertEquals(List.of(added), initial.value());
                            assertTrue(added.isEmpty());
                        }
                ),
                nodeTest(
                        "ListNode#addList(int)",
                        ListNode.create(),
                        node -> node.addList(10),
                        (initial, added) -> {
                            assertEquals(List.of(added), initial.value());
                            assertTrue(added.isEmpty());
                        }
                ),
                nodeTest(
                        "ListNode#addMap()",
                        ListNode.create(),
                        node -> {
                            node.addMap().set("a", "b");
                            return node;
                        },
                        (initial, modified) -> {
                            var expectedMap = MapNode.create(Map.of("a", "b"));
                            assertEquals(1, modified.value().size()); // ListNode#value has one element only.
                            NodeAssertion.assertEquals(expectedMap, modified.value().get(0));
                            NodeAssertion.assertEquals(expectedMap, modified.get(0));
                        }
                ),
                nodeTest(
                        "ListNode#clear",
                        ListNode.create(List.of("a", "b", "c")),
                        node -> {
                            node.clear();
                            return node;
                        },
                        (initial, modified) -> {
                            assertTrue(modified.isEmpty());
                        }
                ),
                nodeTest(
                        "ListNode#contains(Object)",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertTrue(node.contains("a"));
                            assertTrue(node.contains(1));
                            assertTrue(node.contains(null));
                            assertFalse(node.contains(true));
                        }
                ),
                nodeTest(
                        "ListNode#contains(Object) by Node",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertTrue(node.contains(StringValue.fromString("a")));
                            assertTrue(node.contains(new IntValue(1)));
                            assertTrue(node.contains(NullNode.NULL));
                            assertFalse(node.contains(BooleanValue.TRUE));
                        }
                ),
                nodeTest(
                        "ListNode#get(int)",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertEquals(StringValue.fromString("a"), node.get(0));
                            assertEquals(new IntValue(1), node.get(1));
                            assertEquals(NullNode.NULL, node.get(2));
                        }
                ),
                nodeTest(
                        "ListNode#remove(int)",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertEquals(StringValue.fromString("a"), node.remove(0));
                            assertEquals(new IntValue(1), node.remove(0));
                            assertEquals(NullNode.NULL, node.remove(0));
                            return node;
                        },
                        (initial, modified) -> assertTrue(modified.isEmpty())
                ),
                nodeTest(
                        "ListNode#remove(Object)",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertTrue(node.remove("a"));
                            assertTrue(node.remove((Integer) 1));
                            assertTrue(node.remove(null));
                            return node;
                        },
                        (initial, modified) -> assertTrue(modified.isEmpty())
                ),
                nodeTest(
                        "ListNode#remove(Object) by Node",
                        ListNode.create(List.of("a", 1, NullNode.NULL)),
                        node -> {
                            assertTrue(node.remove(StringValue.fromString("a")));
                            assertTrue(node.remove(new IntValue(1)));
                            assertTrue(node.remove(NullNode.NULL));
                            return node;
                        },
                        (initial, modified) -> assertTrue(modified.isEmpty())
                ),
                nodeTest(
                        "ListNode#removeIf(Predicate)",
                        ListNode.create(List.of(1, 10, 1.1, 10.0, 10.0f)),
                        node -> {
                            assertTrue(node.removeIf(DoubleValue.class::isInstance));
                            return node;
                        },
                        (initial, modified) -> assertEquals(List.of(new IntValue(1), new IntValue(10), new FloatValue(10.0f)), modified.value())
                ),
                nodeTest(
                        "ListNode#replaceAll(UnaryOperator)",
                        ListNode.create(List.of(3, 2, 1, 0)),
                        node -> {
                            node.replaceAll(v -> {
                                var value = (IntValue) v;
                                return value.asInt() == 0 ? null : StringValue.fromString(value.asString());
                            });
                            return node;
                        },
                        (initial, modified) -> assertEquals(List.of(StringValue.fromString("3"), StringValue.fromString("2"), StringValue.fromString("1"), NullNode.NULL), modified.value())
                ),
                nodeTest(
                        "ListNode#set(int, Object)",
                        ListNode.create(List.of("a", "b", "d")),
                        node -> {
                            node.set(2, "c");
                            return node;
                        },
                        (initial, modified) -> assertEquals(Stream.of("a", "b", "c").map(StringValue::fromString).toList(), modified.value())
                ),
                nodeTest(
                        "ListNode#size()",
                        ListNode.create(List.of("a", "b", "c")),
                        node -> {
                            assertEquals(3, node.size());
                            assertEquals(node.size(), node.value().size());
                        }
                ),
                nodeTest(
                        "ListNode#sort(Comparator)",
                        ListNode.create(List.of("d", "g", "a", "c", "b", "f", "e")),
                        node -> {
                            node.sort(Comparator.comparing(n -> ((StringValue) n).asString()));
                            return node;
                        },
                        (initial, modified) -> assertEquals(Stream.of("a", "b", "c", "d", "e", "f", "g").map(StringValue::fromString).toList(), modified.value())
                ),
                nodeTest(
                        "ListNode#copy()",
                        ListNode.create(List.of("a", "b", "c")),
                        ListNode::copy,
                        (initial, copied) -> {
                            assertNotSame(initial, copied);

                            initial.add("d");
                            copied.add("D");
                            assertEquals(Stream.of("a", "b", "c", "d").map(StringValue::fromString).toList(), initial.value());
                            assertEquals(Stream.of("a", "b", "c", "D").map(StringValue::fromString).toList(), copied.value());

                            copied.setComment(COMMENT);
                            assertNull(initial.getCommentOrNull());
                            assertSame(COMMENT, copied.getComment());
                        }
                ),
                nodeTest(
                        "ListNode#asView()",
                        ListNode.create(List.of("a", "b", "c")),
                        ListNode::asView,
                        (initial, view) -> {
                            assertNotSame(initial, view);

                            initial.add("d");
                            assertThrows(UnsupportedOperationException.class, () -> view.add("D"));
                            assertEquals(Stream.of("a", "b", "c", "d").map(StringValue::fromString).toList(), initial.value());
                            assertEquals(Stream.of("a", "b", "c", "d").map(StringValue::fromString).toList(), view.value());

                            assertThrows(UnsupportedOperationException.class, () -> view.setComment(COMMENT));
                            assertNull(initial.getCommentOrNull());
                            assertNull(view.getCommentOrNull());

                            initial.setComment(COMMENT);
                            assertSame(COMMENT, initial.getComment());
                            assertSame(COMMENT, view.getComment());
                        }
                )
        );
    }

    @Override
    protected ListNode cast(Node<?> node) {
        return Assertions.assertInstanceOf(ListNode.class, node);
    }

    @Override
    protected Stream<FromObjectTestCase<ListNode>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTest(new ArrayList<>(), node -> {
                    assertTrue(node.isEmpty());
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                }),
                fromObjectTest(List.of("a", "b", "c"), node -> {
                    assertEquals(3, node.size());
                    assertEquals(List.of(StringValue.fromString("a"), StringValue.fromString("b"), StringValue.fromString("c")), node.value());
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                }),
                fromObjectTest(Set.of(1), node -> {
                    assertEquals(1, node.size());
                    assertEquals(List.of(new IntValue(1)), node.value());
                    assertDoesNotThrow(() -> node.add(NullNode.NULL));
                })
        );
    }

    @Override
    protected Stream<ListNode> commentableNodes() {
        return Stream.of(ListNode.create());
    }
}
