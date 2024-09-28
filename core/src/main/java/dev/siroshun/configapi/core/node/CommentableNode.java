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

import dev.siroshun.configapi.core.comment.Comment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface indicating that the {@link Node} can have the {@link Comment}.
 *
 * @param <T> a type of object
 */
@ApiStatus.Experimental
public sealed interface CommentableNode<T> extends Node<T> permits CommentedNode, ListNode, MapNode {

    /**
     * Creates a {@link CommentableNode} with the specified {@link Comment}.
     * <p>
     * If {@code target} is a {@link CommentableNode}, set the specified {@link Comment} using {@link CommentableNode#setComment(Comment)}.
     * Otherwise, wrap the {@code target} in {@link CommentedNode} and attach the {@link Comment}.
     *
     * @param target  the {@link Node} to attach the comment to
     * @param comment the {@link Comment} to attach
     * @param <T>     a type of object
     * @return the {@link CommentableNode}
     */
    static <T> @NotNull CommentableNode<T> withComment(@NotNull Node<T> target, @Nullable Comment comment) {
        if (target instanceof CommentableNode<T> commentableNode) {
            commentableNode.setComment(comment);
            return commentableNode;
        } else {
            return new CommentedNode<>(target, comment);
        }
    }

    /**
     * Checks if this {@link CommentableNode} has the {@link Comment}.
     *
     * @return {@code true} if this {@link CommentableNode} has the {@link Comment}, otherwise {@code false}
     */
    boolean hasComment();

    /**
     * Gets the {@link Comment}.
     *
     * @return the {@link Comment}
     * @throws IllegalStateException if this {@link CommentableNode} does not have the {@link Comment}
     */
    @NotNull Comment getComment();

    /**
     * Gets the {@link Comment} or {@code null} if this {@link CommentableNode} does not have the {@link Comment}.
     *
     * @return the {@link Comment} or {@code null} if this {@link CommentableNode} does not have the {@link Comment}
     */
    default @Nullable Comment getCommentOrNull() {
        return this.hasComment() ? this.getComment() : null;
    }

    /**
     * Sets the {@link Comment}.
     *
     * @param comment the {@link Comment} or {@code null} to remove the current {@link Comment}
     */
    void setComment(@Nullable Comment comment);

}
