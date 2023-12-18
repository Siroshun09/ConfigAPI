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
import com.github.siroshun09.configapi.core.node.visitor.NodeVisitor;
import com.github.siroshun09.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

/**
 * An implementation class of the {@link CommentableNode} to wrap the non-{@link CommentableNode} node and attach the {@link Comment}.
 *
 * @param <T> a type of object
 */
public final class CommentedNode<T> implements CommentableNode<T> {

    private final Node<T> node;
    private @Nullable Comment comment;

    CommentedNode(@NotNull Node<T> node, @Nullable Comment comment) {
        this.node = node;
        this.comment = comment;
    }

    @Override
    public @UnknownNullability T value() {
        return this.node.value();
    }

    /**
     * Gets the wrapped {@link Node}.
     *
     * @return the wrapped {@link Node}
     */
    public @NotNull Node<T> node() {
        return this.node;
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
        this.comment = comment;
    }

    @Override
    public @NotNull VisitResult accept(@NotNull NodeVisitor visitor) {
        var result = visitor.visit(this);
        return result == VisitResult.CONTINUE ? this.node.accept(visitor) : result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CommentedNode<?> that = (CommentedNode<?>) object;
        return this.node.equals(that.node) && Objects.equals(this.comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, comment);
    }

    @Override
    public String toString() {
        return "CommentedNode{" +
                "comment=" + this.comment +
                ", node=" + node +
                '}';
    }
}
