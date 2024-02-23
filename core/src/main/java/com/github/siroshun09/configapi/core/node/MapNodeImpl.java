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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

final class MapNodeImpl implements MapNode {

    static final MapNodeImpl EMPTY = new MapNodeImpl(Collections.emptyMap(), true);

    private final Map<Object, Node<?>> backing;
    private final boolean view;

    private @Nullable Comment comment;

    MapNodeImpl(@NotNull Map<Object, Node<?>> backing, boolean view) {
        this.backing = backing;
        this.view = view;
    }

    @Override
    public @UnknownNullability @UnmodifiableView Map<Object, Node<?>> value() {
        return Collections.unmodifiableMap(this.backing);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public @NotNull Node<?> getOrDefault(@NotNull Object key, @NotNull Node<?> defaultNode) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultNode);
        return this.backing.getOrDefault(key, defaultNode);
    }

    @Override
    public @NotNull Node<?> set(@NotNull Object key, @Nullable Object value) {
        Objects.requireNonNull(key);

        Node<?> removed = this.backing.remove(key);

        if (value != null && value != NullNode.NULL) {
            if (removed instanceof CommentableNode<?> commentableNode) {
                this.backing.put(key, CommentableNode.withComment(Node.fromObject(value), commentableNode.getCommentOrNull()));
            } else {
                this.backing.put(key, Node.fromObject(value));
            }
        }

        return removed != null ? removed : NullNode.NULL;
    }

    @Override
    public Node<?> setIfAbsent(@NotNull Object key, @NotNull Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return this.backing.putIfAbsent(key, Node.fromObject(value));
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Override
    public @NotNull MapNode copy() {
        var copied = MapNode.create(this.backing);
        copied.setComment(this.comment);
        return copied;
    }

    @Override
    public @NotNull @UnmodifiableView MapNode asView() {
        var view = new MapNodeImpl(Collections.unmodifiableMap(this.backing), true);
        view.comment = this.comment;
        return view;
    }

    @Override
    public @NotNull ListNode createList(@NotNull Object key) {
        var newNode = ListNode.create();
        this.backing.put(key, newNode);
        return newNode;
    }

    @Override
    public @NotNull @Unmodifiable MapNode createMap(@NotNull Object key) {
        var newNode = MapNode.create();
        this.backing.put(key, newNode);
        return newNode;
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
            throw new UnsupportedOperationException("Cannot change the comment of this MapNode because this is view mode.");
        }
        this.comment = comment;
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        switch (visitor.startMap(this)) {
            case SKIP -> {
                return VisitResult.SKIP;
            }
            case STOP -> {
                return VisitResult.STOP;
            }
        }

        int index = 0;
        for (var entry : this.backing.entrySet()) {
            var result = switch (visitor.visitEntry(index++, entry.getKey(), entry.getValue())) {
                case CONTINUE -> entry.getValue().accept(visitor);
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

        return visitor.endMap(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapNodeImpl other = (MapNodeImpl) o;
        return this.backing == other.backing; // If they do not refer to the same Map, assume they are different.
    }

    @Override
    public int hashCode() {
        return this.backing.hashCode();
    }

    @Override
    public String toString() {
        return "MapNodeImpl{" +
                "comment=" + this.comment +
                ", backing=" + this.backing +
                '}';
    }
}
