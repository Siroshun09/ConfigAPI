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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectNodeTest extends AbstractNodeTest<ObjectNode<?>> {

    private static final Object OBJECT = new Object();

    @Override
    protected Stream<NodeTestCase<ObjectNode<?>>> nodeTestCases() {
        return Stream.of(
                nodeTest(
                        "ObjectNode",
                        new ObjectNode<>(OBJECT),
                        node -> {
                            assertEquals(OBJECT, node.value());
                            assertTrue(node.hasValue());
                        }
                ),
                nodeTest(
                        "ObjectNode with null",
                        new ObjectNode<>(null),
                        node -> {
                            assertNull(node.value());
                            assertFalse(node.hasValue());
                        }
                )
        );
    }

    @Override
    protected ObjectNode<?> cast(Node<?> node) {
        return (ObjectNode<?>) node;
    }

    @Override
    protected Stream<FromObjectTestCase<ObjectNode<?>>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTest(OBJECT, node -> {
                    assertEquals(OBJECT, node.value());
                    assertTrue(node.hasValue());
                })
        );
    }
}
