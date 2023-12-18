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

import com.github.siroshun09.configapi.core.node.visitor.NodeVisitor;
import com.github.siroshun09.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

/**
 * An interface that holds an object or value
 *
 * @param <T> a type of object
 */
public sealed interface Node<T> permits ArrayNode, CommentableNode, NullNode, ObjectNode, ValueNode {

    /**
     * Creates a {@link Node} from the specified object.
     * <p>
     * This method is implemented with the following specifications:
     *
     * <ul>
     *     <li>If the given object is null or {@link NullNode#NULL}, returns {@link NullNode#NULL}</li>
     *     <li>If the given object is {@link Node}:
     *     <ul>
     *         <li>If the {@link Node} is implemented {@link ValueNode}, returns it as-is</li>
     *         <li>If the {@link Node} is {@link ListNode}, copies it by using {@link ListNode#copy()}</li>
     *         <li>If the {@link Node} is {@link MapNode}, copies it by using {@link MapNode#copy()}</li>
     *         <li>If the {@link Node} is {@link CommentedNode}, recreate it with {@link CommentedNode#node()} that is passed to this method</li>
     *         <li>Other {@link Node}s will be re-created</li>
     *     </ul>
     *     </li>
     *     <li>If the given object is a array:
     *     <ul>
     *         <li>If the array is a primitive array, returns {@link ArrayNode}. (the array will NOT be copied)</li>
     *         <li>If the array is an object array, returns {@link ListNode}.</li>
     *     </ul>
     *     </li>
     *     <li>If the given object is {@link String}/{@link Enum}, creates {@link StringValue}/{@link EnumValue}</li>
     *     <li>If the given object is {@link Number}, passes the number to {@link NumberValue#fromNumber(Number)}</li>
     *     <li>If the given object is {@link Boolean}, passes the boolean to {@link BooleanValue#fromBoolean(Boolean)}</li>
     *     <li>If the given object is implemented {@link java.util.Collection}, creates {@link ListNode} using {@link ListNode#create(java.util.Collection)}</li>
     *     <li>If the given object is implemented {@link java.util.Map}, creates {@link MapNode} using {@link MapNode#create(java.util.Map)}</li>
     *     <li>Otherwise, returns {@link ObjectNode} with the given object</li>
     * </ul>
     *
     * @param obj the object to create a {@link Node}
     * @return a {@link Node}
     */
    static @NotNull Node<?> fromObject(@Nullable Object obj) {
        return NodeUtils.toNode(obj);
    }

    /**
     * Gets an object which this {@link Node} holds
     *
     * @return an object which this {@link Node} holds
     */
    @UnknownNullability T value();

    /**
     * Checks if this {@link Node} holds an object.
     * <p>
     * The default implementation of this method checks if the returning value from {@link #value()} is not {@code null}.
     *
     * @return {@code true} if this {@link Node} holds an object, otherwise {@code false}
     */
    default boolean hasValue() {
        return value() != null;
    }

    /**
     * Gets an object as {@link Optional}.
     * <p>
     * The default implementation of this method returns {@link Optional#ofNullable(Object)} which is wrapping {@link #value()}.
     *
     * @return the {@link Optional}
     */
    default @NotNull Optional<T> asOptional() {
        return Optional.ofNullable(value());
    }

    /**
     * Accepts a {@link NodeVisitor} for this {@link Node}.
     *
     * @param visitor a {@link NodeVisitor} to accept
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult accept(@NotNull NodeVisitor visitor);

}
