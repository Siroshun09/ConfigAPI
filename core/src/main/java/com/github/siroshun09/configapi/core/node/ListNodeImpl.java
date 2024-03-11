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

import com.github.siroshun09.configapi.core.comment.Comment;
import com.github.siroshun09.configapi.core.node.visitor.NodeVisitor;
import com.github.siroshun09.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
    public void add(@Nullable Object value) {
        this.backing.add(Node.fromObject(value));
    }

    @Override
    public void addAll(@NotNull Collection<?> collection) {
        for (var element : collection) {
            this.backing.add(Node.fromObject(element));
        }
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
    public void clear() {
        this.backing.clear();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.backing.contains(Node.fromObject(object));
    }

    @Override
    public @NotNull Node<?> get(int index) {
        return this.backing.get(index);
    }

    @Override
    public boolean isEmpty() {
        return this.backing.isEmpty();
    }

    @Override
    public boolean remove(@Nullable Object value) {
        return this.backing.remove(Node.fromObject(value));
    }

    @Override
    public void removeIf(@NotNull Predicate<Node<?>> predicate) {
        this.backing.removeIf(predicate);
    }

    @Override
    public void replaceAll(@NotNull UnaryOperator<Node<?>> operator) {
        this.backing.replaceAll(node -> Objects.requireNonNullElse(operator.apply(node), NullNode.NULL));
    }

    @Override
    public @NotNull Node<?> set(int index, @Nullable Object object) {
        return this.backing.set(index, Node.fromObject(object));
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public void sort(@NotNull Comparator<? super Node<?>> comparator) {
        this.backing.sort(comparator);
    }

    @Override
    public @NotNull Node<?> remove(int index) {
        return this.backing.remove(index);
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
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        switch (visitor.startList(this)) {
            case SKIP -> {
                return VisitResult.SKIP;
            }
            case STOP -> {
                return VisitResult.STOP;
            }
        }

        for (int i = 0, backingSize = this.backing.size(); i < backingSize; i++) {
            var node = this.backing.get(i);
            var result = switch (visitor.visitElement(i, node)) {
                case CONTINUE -> node.accept(visitor);
                case BREAK -> VisitResult.BREAK;
                case SKIP -> VisitResult.SKIP;
                case STOP -> VisitResult.STOP;
            };

            if (result == VisitResult.BREAK) {
                break;
            } else if (result == VisitResult.STOP) {
                return VisitResult.STOP;
            }
        }

        return visitor.endList(this);
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

    private static <T> @Nullable T castIfPossible(@NotNull Node<?> node, @NotNull Class<? extends T> clazz) {
        if (node instanceof CommentedNode<?> commented) {
            return castIfPossible(commented.node(), clazz);
        }

        if (clazz.isInstance(node)) {
            return clazz.cast(node);
        } else if (clazz.isInstance(node.value())) {
            return clazz.cast(node.value());
        } else if (clazz == String.class && node instanceof StringRepresentable stringRepresentable) {
            return clazz.cast(stringRepresentable.asString());
        } else {
            return null;
        }
    }
}
