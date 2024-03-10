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

import java.util.ArrayList;
import java.util.List;

class ListNodeTest {

    private static final SimpleComment COMMENT = SimpleComment.create("test");

    @Test
    void testCreate() {
        var listNode = ListNode.create();
        Assertions.assertTrue(listNode.value().isEmpty());

        // checks if the list is modifiable
        Assertions.assertDoesNotThrow(() -> listNode.add(1));
        Assertions.assertDoesNotThrow(() -> listNode.setComment(COMMENT));
    }

    @Test
    void testCreateWithCollection() {
        var original = new ArrayList<>(List.of("a", "b", "c"));
        var listNode = ListNode.create(original);

        Assertions.assertEquals(3, listNode.value().size());

        // Checks if the ListNode is not modified by adding the element to the original list.
        original.add("d");
        Assertions.assertEquals(3, listNode.value().size());

        // checks if the list is modifiable
        Assertions.assertDoesNotThrow(() -> listNode.add(1));
        Assertions.assertDoesNotThrow(() -> listNode.setComment(COMMENT));
        Assertions.assertDoesNotThrow(() -> ListNode.create(List.of(1, 2, 3)).add(4));
    }

    @Test
    void testEmpty() {
        Assertions.assertTrue(ListNode.empty().value().isEmpty());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> ListNode.empty().add(4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> ListNode.empty().setComment(COMMENT));
    }

    @Test
    void testValue() {
        var listNode = ListNode.create(List.of("a", "b", "c"));
        Assertions.assertEquals(List.of(new StringValue("a"), new StringValue("b"), new StringValue("c")), listNode.value());
    }

    @Test
    void testHasValue() {
        Assertions.assertTrue(ListNode.create().hasValue());
        Assertions.assertTrue(ListNode.create(List.of("a", "b", "c")).hasValue());
        Assertions.assertTrue(ListNode.empty().hasValue());
    }

    @Test
    void testAsList() {
        var original = List.of("a", "b", "c");
        var listNode = ListNode.create(original);
        Assertions.assertEquals(original, listNode.asList(String.class));

        Assertions.assertEquals(List.of(), ListNode.create().asList(String.class));
        Assertions.assertEquals(List.of(), ListNode.empty().asList(String.class));
    }

    @Test
    void testAdd() {
        var listNode1 = ListNode.create();
        listNode1.add("a");
        Assertions.assertEquals(1, listNode1.value().size());
        Assertions.assertEquals(List.of("a"), listNode1.asList(String.class));

        var listNode2 = ListNode.create(List.of("a", "b", "c"));
        Assertions.assertEquals(3, listNode2.value().size());
        listNode2.add("d");
        Assertions.assertEquals(4, listNode2.value().size());
        Assertions.assertEquals(List.of("a", "b", "c", "d"), listNode2.asList(String.class));
    }

    @Test
    void testAddAll() {
        var listNode1 = ListNode.create();
        listNode1.addAll(List.of("a", "b", "c"));
        Assertions.assertEquals(3, listNode1.value().size());
        Assertions.assertEquals(List.of("a", "b", "c"), listNode1.asList(String.class));

        var listNode2 = ListNode.create(List.of("a", "b", "c"));
        Assertions.assertEquals(3, listNode2.value().size());
        listNode2.addAll(List.of("d", "e", "f"));
        Assertions.assertEquals(6, listNode2.value().size());
        Assertions.assertEquals(List.of("a", "b", "c", "d", "e", "f"), listNode2.asList(String.class));
    }

    @Test
    void testRemove() {
        var listNode = ListNode.create(List.of("a", "b", "c"));
        Assertions.assertEquals(3, listNode.value().size());

        listNode.remove("c");
        Assertions.assertEquals(2, listNode.value().size());
        Assertions.assertEquals(List.of("a", "b"), listNode.asList(String.class));

        listNode.remove("d");
        Assertions.assertEquals(2, listNode.value().size());
        Assertions.assertEquals(List.of("a", "b"), listNode.asList(String.class));
    }

    @Test
    void testRemoveIf() {
        var listNode = ListNode.create(List.of(1, 10, 1.1, 10.0, 10.0f));
        listNode.removeIf(DoubleValue.class::isInstance);
        Assertions.assertEquals(3, listNode.value().size());
    }

    @Test
    void testClear() {
        var listNode = ListNode.create(List.of("a", "b", "c"));
        Assertions.assertEquals(3, listNode.value().size());
        listNode.clear();
        Assertions.assertTrue(listNode.value().isEmpty());
    }

    @Test
    void testCopy() {
        var listNode = ListNode.create(List.of("a", "b", "c"));
        listNode.setComment(COMMENT);

        var copied = listNode.copy();

        listNode.add("d");
        copied.add("e");

        Assertions.assertEquals(List.of("a", "b", "c", "d"), listNode.asList(String.class));
        Assertions.assertEquals(List.of("a", "b", "c", "e"), copied.asList(String.class));

        Assertions.assertEquals(listNode.getCommentOrNull(), copied.getCommentOrNull());
    }

    @Test
    void testAsView() {
        var listNode = ListNode.create(List.of("a", "b", "c"));
        var view = listNode.asView();

        listNode.add("d");

        Assertions.assertEquals(List.of("a", "b", "c", "d"), listNode.asList(String.class));
        Assertions.assertEquals(List.of("a", "b", "c", "d"), view.asList(String.class));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.add("e"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.setComment(COMMENT));

        Assertions.assertEquals(List.of("a", "b", "c", "d"), listNode.asList(String.class));
        Assertions.assertEquals(List.of("a", "b", "c", "d"), view.asList(String.class));
    }
}
