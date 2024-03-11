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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A {@link Node} implementation that represents a {@link List} of {@link Node}s.
 */
public sealed interface ListNode extends CommentableNode<List<Node<?>>> permits ListNodeImpl {

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
        return new ListNodeImpl(new ArrayList<>(), false);
    }

    /**
     * Creates a new {@link ListNode}.
     *
     * @param initialCapacity the initial capacity of the list
     * @return a new {@link ListNode}
     */
    static @NotNull ListNode create(int initialCapacity) {
        return new ListNodeImpl(new ArrayList<>(initialCapacity), false);
    }

    /**
     * Creates a new {@link ListNode} with values in the given {@link Collection}.
     *
     * @param collection a {@link Collection} to add elements to the new {@link ListNode}
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

        return new ListNodeImpl(converted, false);
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
     * @param elementClass a class to cast elements
     * @param <T>          a type of the class
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
    void add(@Nullable Object value);

    /**
     * Adds new objects in the given {@link Collection} to this {@link ListNode}.
     *
     * @param collection a {@link Collection} that includes new objects to add
     */
    void addAll(@NotNull Collection<?> collection);

    /**
     * Adds a new {@link ListNode} to this {@link ListNode}.
     *
     * @return a created {@link ListNode}
     */
    @NotNull ListNode addList();

    /**
     * Adds a new {@link ListNode} to this {@link ListNode}.
     *
     * @param initialCapacity the initial capacity of the list
     * @return a created {@link ListNode}
     */
    @NotNull ListNode addList(int initialCapacity);

    /**
     * Adds a new {@link MapNode} to this {@link ListNode}.
     *
     * @return a created {@link MapNode}
     */
    @NotNull MapNode addMap();

    /**
     * Clears this {@link ListNode}.
     */
    void clear();

    /**
     * Checks if the specified object is contained in this {@link ListNode}.
     *
     * @param object the object to check
     * @return {@code true} if the specified object is contained in this {@link ListNode}, otherwise {@code false}
     */
    boolean contains(@Nullable Object object);

    /**
     * Gets the element at the specified position in this {@link ListNode}.
     *
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @NotNull Node<?> get(int index);

    /**
     * Checks if this {@link ListNode} has no element.
     *
     * @return {@code true} if this {@link ListNode} has no element, otherwise {@code false}
     */
    boolean isEmpty();

    /**
     * Removes the element at the specified position in this {@link ListNode}.
     *
     * @param index the index of the element to remove
     * @return the removed {@link Node}
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @NotNull Node<?> remove(int index);

    /**
     * Removes an object from this {@link ListNode}.
     *
     * @param value an object to remove from this {@link ListNode}
     * @return {@code true} if the specified object is removed, otherwise {@code false}
     */
    boolean remove(@Nullable Object value);

    /**
     * Removes the elements from this {@link ListNode} that satisfy the given predicate.
     *
     * @param predicate a predicate which returns {@code true} for elements to be removed
     */
    void removeIf(@NotNull Predicate<? super Node<?>> predicate);

    /**
     * Replaces each element in this {@link ListNode} using the given {@link UnaryOperator}.
     *
     * @param operator a {@link UnaryOperator} that replaces the elements
     */
    void replaceAll(@NotNull UnaryOperator<Node<?>> operator);

    /**
     * Sets the new element at the specified position in this {@link ListNode}.
     *
     * @param index  the index of the element to set
     * @param object the object to set
     * @return the removed {@link Node}
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @NotNull Node<?> set(int index, @Nullable Object object);

    /**
     * Returns the number of elements in this {@link ListNode}.
     *
     * @return the number of elements in this {@link ListNode}
     */
    int size();

    /**
     * Sorts this {@link ListNode} using the given {@link Comparator}.
     *
     * @param comparator the {@link Comparator} to use for sorting this {@link ListNode}
     */
    void sort(@NotNull Comparator<? super Node<?>> comparator);

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
