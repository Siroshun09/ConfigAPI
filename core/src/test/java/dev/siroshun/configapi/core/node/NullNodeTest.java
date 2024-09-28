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


import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class NullNodeTest extends AbstractNodeTest<NullNode> {

    @Override
    protected Stream<NodeTestCase<NullNode>> nodeTestCases() {
        return Stream.of(nodeTest("NullNode", NullNode.NULL, node -> {
            assertNull(node.value());
            assertFalse(node.hasValue());
            assertEquals(Optional.empty(), node.asOptional());
        }));
    }

    @Override
    protected NullNode cast(Node<?> node) {
        return (NullNode) node;
    }

    @Override
    protected Stream<FromObjectTestCase<NullNode>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTest(null, node -> assertSame(NullNode.NULL, node)),
                fromObjectTest(NullNode.NULL, node -> assertSame(NullNode.NULL, node))
        );
    }
}
