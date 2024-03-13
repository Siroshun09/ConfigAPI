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

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractNodeTest<N extends Node<?>> {

    @ParameterizedTest
    @MethodSource("nodeTestCases")
    void testNode(NodeTestCase<N> testCase) {
        var modified = testCase.modifier().apply(testCase.initialNode());
        testCase.tester().accept(testCase.initialNode(), modified);
    }

    protected abstract Stream<NodeTestCase<N>> nodeTestCases();

    @ParameterizedTest
    @MethodSource("fromObjectTestCases")
    void testFromObject(FromObjectTestCase<N> testCase) {
        var node = Node.fromObject(testCase.object());
        testCase.tester().accept(this.cast(node));
    }

    protected abstract N cast(Node<?> node);

    protected abstract Stream<FromObjectTestCase<N>> fromObjectTestCases();

    protected static <N extends Node<?>> NodeTestCase<N> nodeTest(String subject, N initialNode, Consumer<N> tester) {
        return nodeTest(subject, initialNode, UnaryOperator.identity(), (initial, modified) -> tester.accept(initial));
    }

    protected static <N extends Node<?>> NodeTestCase<N> nodeTest(String subject, N initialNode, UnaryOperator<N> modifier,
                                                                  BiConsumer<N, N> tester) {
        return new NodeTestCase<>(subject, initialNode, modifier, tester);
    }

    protected record NodeTestCase<N extends Node<?>>(
            String subject,
            N initialNode,
            UnaryOperator<N> modifier,
            BiConsumer<N, N> tester) {
    }

    protected static <N extends Node<?>> FromObjectTestCase<N> fromObjectTest(Object object, Consumer<N> tester) {
        return new FromObjectTestCase<>(object, tester);
    }

    protected record FromObjectTestCase<N extends Node<?>>(Object object, Consumer<N> tester) {
    }
}
