/*
 *     Copyright 2023 Siroshun09
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

import com.github.siroshun09.configapi.core.comment.Comment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class ListNodeImpl implements ListNode {

    static final ListNodeImpl EMPTY = new ListNodeImpl(Collections.emptyList(), true);

    private final List<Node<?>> backing;
    private final boolean view;
    private @Nullable Comment comment;

    ListNodeImpl(@NotNull List<Node<?>> backing, boolean view) {
        this.backing = backing;
        this.view = view;
    }

    @Override
    public @NotNull @UnmodifiableView List<Node<?>> value() {
        return this == EMPTY ? Collections.emptyList() : Collections.unmodifiableList(this.backing);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public <T> @NotNull @Unmodifiable List<T> asList(@NotNull Class<? extends T> elementClass) {
        if (this.backing.isEmpty()) {
            return Collections.emptyList();
        }

        Objects.requireNonNull(elementClass);

        var result = new ArrayList<T>(this.backing.size());

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, backingSize = this.backing.size(); i < backingSize; i++) {
            var element = this.backing.get(i);
            var casted = castIfPossible(element, elementClass);
            if (casted != null) {
                result.add(casted);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public @NotNull Stream<Node<?>> stream() {
        return this.backing.stream();
    }

    @Override
    public void add(@NotNull Object value) {
        this.backing.add(Node.fromObject(value));
    }

    @Override
    public @NotNull ListNode addList() {
        var listNode = ListNode.create();
        this.backing.add(listNode);
        return listNode;
    }

    @Override
    public @NotNull ListNode addList(int initialCapacity) {
        var listNode = ListNode.create(initialCapacity);
        this.backing.add(listNode);
        return listNode;
    }

    @Override
    public @NotNull MapNode addMap() {
        var listNode = MapNode.create();
        this.backing.add(listNode);
        return listNode;
    }

    @Override
    public void remove(@NotNull Object value) {
        this.backing.remove(Node.fromObject(value));
    }

    @Override
    public void removeIf(@NotNull Predicate<Node<?>> predicate) {
        this.backing.removeIf(predicate);
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Contract(" -> new")
    @Override
    public @NotNull ListNode copy() {
        ListNode copied;

        if (this.backing.isEmpty()) {
            copied = ListNode.create();
        } else {
            var copiedList = new ArrayList<Node<?>>(this.backing.size());

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, backingSize = this.backing.size(); i < backingSize; i++) {
                copiedList.add(Node.fromObject(this.backing.get(i)));
            }

            copied = new ListNodeImpl(copiedList, false);
        }

        copied.setComment(this.comment);
        return copied;
    }

    @Contract(" -> new")
    @Override
    public @NotNull @UnmodifiableView ListNode asView() {
        return this == EMPTY ? EMPTY : new ListNodeImpl(Collections.unmodifiableList(this.backing), true);
    }

    @Override
    public boolean hasComment() {
        return this.comment != null;
    }

    @Override
    public @NotNull Comment getComment() {
        if (this.comment == null) {
            throw new IllegalStateException("Comment is not set.");
        }
        return this.comment;
    }

    @Override
    public void setComment(@Nullable Comment comment) {
        if (this.view) {
            throw new UnsupportedOperationException("Cannot change the comment of this ListNode because this is view mode.");
        }
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListNodeImpl other = (ListNodeImpl) o;
        return this.backing == other.backing;  // If they do not refer to the same list assume they are different.
    }

    @Override
    public int hashCode() {
        return this.backing.hashCode();
    }

    @Override
    public String toString() {
        return "ListNodeImpl{" +
                "comment=" + this.comment +
                ", backing=" + this.backing +
                '}';
    }

    @Override
    public void appendValue(@NotNull StringBuilder builder) {
        int iMax = this.backing.size() - 1;

        if (iMax == -1) {
            builder.append("[]");
        } else {
            builder.append('[');

            for (int i = 0; i <= iMax; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                this.backing.get(i).appendValue(builder);
            }

            builder.append(']');
        }
    }

    private static <T> @Nullable T castIfPossible(@NotNull Node<?> node, @NotNull Class<? extends T> clazz) {
        if (clazz.isInstance(node)) {
            return clazz.cast(node);
        } else if (clazz.isInstance(node.value())) {
            return clazz.cast(node.value());
        } else {
            return null;
        }
    }
}
