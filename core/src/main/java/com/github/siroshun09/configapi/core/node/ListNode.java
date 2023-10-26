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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A {@link Node} implementation that represents a {@link List} of {@link Node}s.
 */
public sealed interface ListNode extends Node<List<Node<?>>> permits ListNodeImpl {

    /**
     * An implementation {@link Class} of this interface.
     */
    @ApiStatus.Internal
    Class<? extends ListNode> IMPLEMENTATION_CLASS = ListNodeImpl.class;

    /**
     * Creates a new {@link ListNode}.
     *
     * @return a new {@link ListNode}
     */
    static @NotNull ListNode create() {
        return new ListNodeImpl(new ArrayList<>());
    }

    /**
     * Creates a new {@link ListNode} with values in the given {@link Collection}.
     *
     * @return a new {@link ListNode} with values in the given {@link Collection}
     */
    static @NotNull ListNode create(@NotNull Collection<?> collection) {
        if (collection.isEmpty()) {
            return create();
        }

        var converted = new ArrayList<Node<?>>(collection.size());

        for (var element : collection) {
            converted.add(Node.fromObject(element));
        }

        return new ListNodeImpl(converted);
    }

    /**
     * Gets a {@link ListNode} that is always empty.
     * <p>
     * The returning {@link ListNode} cannot be modified using methods like {@link #add(Object)}.
     *
     * @return a {@link ListNode} that is always empty
     */
    static @NotNull @Unmodifiable ListNode empty() {
        return ListNodeImpl.EMPTY;
    }

    /**
     * Gets a {@link List} that this {@link ListNode} has.
     * <p>
     * The returning {@link List} cannot be modified, but the elements in the list may be changed by other codes.
     *
     * @return a {@link List} that this {@link ListNode} has
     */
    @Override
    @NotNull @UnmodifiableView List<Node<?>> value();

    /**
     * Gets a {@link List} containing elements of the specified {@link Class}.
     * <p>
     * The returning {@link List} is immutable.
     * <p>
     * The list only contain elements such that {@link Class#isInstance} returns {@code true}.
     * Other elements are ignored.
     *
     * @return a {@link List} containing elements of the specified {@link Class}
     */
    <T> @NotNull @Unmodifiable List<T> asList(@NotNull Class<? extends T> elementClass);

    /**
     * Creates a {@link Stream} from an internal list.
     *
     * @return a {@link Stream} from an internal list
     */
    @NotNull Stream<Node<?>> stream();

    /**
     * Adds a new object to this {@link ListNode}.
     *
     * @param value a new object
     */
    void add(@NotNull Object value);

    /**
     * Removes an object from this {@link ListNode}.
     *
     * @param value an object to remove from this {@link ListNode}
     */
    void remove(@NotNull Object value);

    /**
     * Removes the elements from this {@link ListNode} that satisfy the given predicate.
     *
     * @param predicate a predicate which returns {@code true} for elements to be removed
     */
    void removeIf(@NotNull Predicate<Node<?>> predicate);

    /**
     * Clears this {@link ListNode}.
     */
    void clear();

    /**
     * Copies this {@link ListNode}.
     * <p>
     * The elements in this {@link ListNode} will also be copied using {@link Node#fromObject(Object)}.
     *
     * @return a copied {@link ListNode}
     */
    @Contract("-> new")
    @NotNull ListNode copy();

    /**
     * Gets a view of this {@link ListNode}.
     * <p>
     * The returning {@link ListNode} cannot be modified, but this {@link ListNode} can still be modified,
     * so the elements may be changed by other codes using this instance.
     *
     * @return a view of this {@link ListNode}
     */
    @Contract("-> new")
    @NotNull @UnmodifiableView ListNode asView();

}
