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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class ValueNodeTest extends AbstractNodeTest<ValueNode<?>> {

    // Test cases for ValueNodes are in ValueNodeTest#fromObjectTestCases
    @Override
    @Disabled
    void testNode(NodeTestCase<ValueNode<?>> testCase) {
        super.testNode(testCase);
    }

    @Override
    protected Stream<NodeTestCase<ValueNode<?>>> nodeTestCases() {
        return Stream.of();
    }

    @Override
    void testFromObject(FromObjectTestCase<ValueNode<?>> testCase) {
        super.testFromObject(testCase);
        var node = Node.fromObject(testCase.object());
        assertSame(node, Node.fromObject(node)); // ValueNode should be returned as-is from Node#fromObject
    }

    @Override
    protected ValueNode<?> cast(Node<?> node) {
        return (ValueNode<?>) node;
    }

    @Override
    protected Stream<FromObjectTestCase<ValueNode<?>>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTest(true, node -> assertSame(BooleanValue.TRUE, node)),
                fromObjectTest(false, node -> assertSame(BooleanValue.FALSE, node)),
                fromObjectTest((byte) 1, node -> assertEquals((byte) 1, assertInstanceOf(ByteValue.class, node).value())),
                fromObjectTest('a', node -> assertEquals('a', assertInstanceOf(CharValue.class, node).value())),
                fromObjectTest(3.14, node -> assertEquals(3.14, assertInstanceOf(DoubleValue.class, node).value())),
                fromObjectTest(ExampleEnum.A, node -> assertEquals(ExampleEnum.A, assertInstanceOf(EnumValue.class, node).value())),
                fromObjectTest(3.14f, node -> assertEquals(3.14f, assertInstanceOf(FloatValue.class, node).value())),
                fromObjectTest(1, node -> assertEquals(1, assertInstanceOf(IntValue.class, node).value())),
                fromObjectTest(1L, node -> assertEquals(1L, assertInstanceOf(LongValue.class, node).value())),
                fromObjectTest((short) 1, node -> assertEquals((short) 1, assertInstanceOf(ShortValue.class, node).value())),
                fromObjectTest("a", node -> assertEquals("a", assertInstanceOf(StringValue.class, node).value())),

                // Additional number supports
                fromObjectTest(new AtomicInteger(1), node -> assertEquals(1, assertInstanceOf(IntValue.class, node).value())),
                fromObjectTest(new AtomicLong(1), node -> assertEquals(1L, assertInstanceOf(LongValue.class, node).value()))
                );
    }

    private enum ExampleEnum {
        A
    }

    @Nested
    class BooleanValueTest {
        @Test
        void testFromBoolean() {
            assertSame(BooleanValue.TRUE, BooleanValue.fromBoolean(Boolean.TRUE));
            assertSame(BooleanValue.FALSE, BooleanValue.fromBoolean(Boolean.FALSE));

            assertSame(BooleanValue.TRUE, BooleanValue.fromBoolean(true));
            assertSame(BooleanValue.FALSE, BooleanValue.fromBoolean(false));
        }
    }

    @Nested
    class NumberValueTest {
        @Test
        void testNullForFromNumber() {
            assertSame(NumberValue.ZERO, NumberValue.fromNumber(null));
        }
    }
}
