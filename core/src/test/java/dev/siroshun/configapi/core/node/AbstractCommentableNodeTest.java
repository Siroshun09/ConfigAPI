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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

abstract class AbstractCommentableNodeTest<N extends CommentableNode<?>> extends AbstractNodeTest<N> {

    protected static final SimpleComment COMMENT = SimpleComment.create("test");

    @ParameterizedTest
    @MethodSource("commentableNodes")
    void testCommentableNode(N node) {
        Assertions.assertFalse(node.hasComment());
        Assertions.assertThrows(IllegalStateException.class, node::getComment);
        Assertions.assertNull(node.getCommentOrNull());

        node.setComment(COMMENT);
        Assertions.assertTrue(node.hasComment());
        Assertions.assertSame(COMMENT, node.getComment());
        Assertions.assertSame(COMMENT, node.getCommentOrNull());
    }

    @ParameterizedTest
    @MethodSource("commentableNodes")
    void testWithComment(N node) {
        Assertions.assertSame(node, CommentableNode.withComment((Node<?>) node, COMMENT));
        Assertions.assertTrue(node.hasComment());
        Assertions.assertSame(COMMENT, node.getComment());
        Assertions.assertSame(COMMENT, node.getCommentOrNull());
    }

    protected abstract Stream<N> commentableNodes();
}
