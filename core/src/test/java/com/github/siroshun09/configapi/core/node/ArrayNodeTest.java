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

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayNodeTest extends AbstractNodeTest<ArrayNode<?>> {

    @Override
    protected Stream<NodeTestCase<ArrayNode<?>>> nodeTestCases() {
        return Stream.of(
                arrayNodeTest("BooleanArray", new boolean[]{true, false, true}, BooleanArray::new),
                arrayNodeTest("ByteArray", new byte[]{(byte) -1, (byte) 0, (byte) 1}, ByteArray::new),
                arrayNodeTest("CharArray", new char[]{'a', 'b', 'c'}, CharArray::new),
                arrayNodeTest("DoubleArray", new double[]{-1.5, 0.0, 1.5}, DoubleArray::new),
                arrayNodeTest("FloatArray", new float[]{-1.5f, 0.0f, 1.5f}, FloatArray::new),
                arrayNodeTest("IntArray", new int[]{-1, 0, 1}, IntArray::new),
                arrayNodeTest("LongArray", new long[]{-1L, 0L, 1L}, LongArray::new),
                arrayNodeTest("ShortArray", new short[]{(short) -1, (short) 0, (short) 1}, ShortArray::new)
        );
    }

    private static <T, AN extends ArrayNode<T>> NodeTestCase<ArrayNode<?>> arrayNodeTest(String subject, T array, Function<T, AN> nodeFactory) {
        return nodeTest(subject, nodeFactory.apply(array), node -> {
            assertTrue(node.hasValue());
            assertSame(array, node.value());
        });
    }

    @Override
    protected ArrayNode<?> cast(Node<?> node) {
        return (ArrayNode<?>) node;
    }

    @Override
    protected Stream<FromObjectTestCase<ArrayNode<?>>> fromObjectTestCases() {
        return Stream.of(
                fromObjectTestForArray(new boolean[]{true, false, true}),
                fromObjectTestForArray(new byte[]{(byte) -1, (byte) 0, (byte) 1}),
                fromObjectTestForArray(new char[]{'a', 'b', 'c'}),
                fromObjectTestForArray(new double[]{-1.5, 0.0, 1.5}),
                fromObjectTestForArray(new float[]{-1.5f, 0.0f, 1.5f}),
                fromObjectTestForArray(new int[]{-1, 0, 1}),
                fromObjectTestForArray(new long[]{-1L, 0L, 1L}),
                fromObjectTestForArray(new short[]{(short) -1, (short) 0, (short) 1})
        );
    }

    private static <T> FromObjectTestCase<ArrayNode<?>> fromObjectTestForArray(T array) {
        return fromObjectTest(array, node -> {
            assertTrue(node.hasValue());
            assertSame(array, node.value());
        });
    }
}
