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

import dev.siroshun.configapi.core.comment.SimpleComment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.stream.Stream;

class CommentedNodeTest extends AbstractCommentableNodeTest<CommentedNode<?>> {

    private static final SimpleComment COMMENT = SimpleComment.create("test");

    // Tests for CommentedNode are in AbstractCommentableNodeTest#testCommentableNode
    @Override
    @Disabled
    void testNode(NodeTestCase<CommentedNode<?>> testCase) {
        super.testNode(testCase);
    }

    @Override
    protected Stream<NodeTestCase<CommentedNode<?>>> nodeTestCases() {
        return Stream.of();
    }

    @Override
    protected CommentedNode<?> cast(Node<?> node) {
        return (CommentedNode<?>) node;
    }

    @Override
    protected Stream<FromObjectTestCase<CommentedNode<?>>> fromObjectTestCases() {
        return this.commentableNodes()
                .map(node -> {
                    node.setComment(COMMENT);
                    return fromObjectTest(
                            node,
                            actual -> {
                                Assertions.assertTrue(actual.hasComment());
                                Assertions.assertSame(COMMENT, actual.getComment());
                                Assertions.assertEquals(node.node(), actual.node());
                            }
                    );
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Stream<CommentedNode<?>> commentableNodes() {
        return nonCommentableNodes()
                .map(node -> CommentableNode.withComment((Node<?>) node, null))
                .map(node -> Assertions.assertInstanceOf(CommentedNode.class, node));
    }

    private enum ExampleEnum {
        A
    }

    private static Stream<Node<?>> nonCommentableNodes() {
        return Stream.of(
                new BooleanArray(new boolean[0]),
                BooleanValue.TRUE,
                new ByteArray(new byte[0]),
                new ByteValue((byte) 0),
                new CharArray(new char[0]),
                new CharValue('a'),
                new DoubleArray(new double[0]),
                new DoubleValue(3.14),
                new EnumValue<>(ExampleEnum.A),
                new FloatArray(new float[0]),
                new FloatValue(3.14f),
                new IntArray(new int[0]),
                new IntValue(1),
                new LongArray(new long[0]),
                new LongValue(1L),
                NullNode.NULL,
                new ObjectNode<>(new Object()),
                new ShortArray(new short[0]),
                new ShortValue((short) 1),
                StringValue.fromString("a")
        );
    }
}
