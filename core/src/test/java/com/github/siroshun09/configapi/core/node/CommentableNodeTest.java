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

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommentableNodeTest {

    private static final SimpleComment COMMENT = SimpleComment.create("test");

    @Test
    void testWithComment() {
        var commentedNode = Assertions.assertInstanceOf(CommentedNode.class, CommentableNode.withComment(new StringValue("test"), COMMENT));

        Assertions.assertEquals(new StringValue("test"), commentedNode.node());
        Assertions.assertEquals("test", commentedNode.value());
        Assertions.assertTrue(commentedNode.hasValue());
        Assertions.assertTrue(commentedNode.hasComment());
        Assertions.assertEquals(COMMENT, commentedNode.getComment());
        Assertions.assertEquals(COMMENT, commentedNode.getCommentOrNull());

        var listNode = Assertions.assertInstanceOf(ListNode.class, CommentableNode.withComment(ListNode.create(), COMMENT));

        Assertions.assertTrue(listNode.hasComment());
        Assertions.assertEquals(COMMENT, listNode.getComment());
        Assertions.assertEquals(COMMENT, listNode.getCommentOrNull());

        var mapNode = Assertions.assertInstanceOf(MapNode.class, CommentableNode.withComment(MapNode.create(), COMMENT));

        Assertions.assertTrue(mapNode.hasComment());
        Assertions.assertEquals(COMMENT, mapNode.getComment());
        Assertions.assertEquals(COMMENT, mapNode.getCommentOrNull());
    }
}
