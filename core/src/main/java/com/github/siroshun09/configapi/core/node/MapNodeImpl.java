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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

final class MapNodeImpl implements MapNode {

    static final MapNodeImpl EMPTY = new MapNodeImpl(Collections.emptyMap());

    private final Map<Object, Node<?>> backing;

    MapNodeImpl(@NotNull Map<Object, Node<?>> backing) {
        this.backing = backing;
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
    public @NotNull Node<?> get(@NotNull Object key) {
        Objects.requireNonNull(key);
        return this.backing.getOrDefault(key, NullNode.NULL);
    }

    @Override
    public @NotNull Node<?> set(@NotNull Object key, @Nullable Object value) {
        Objects.requireNonNull(key);

        Node<?> removed;

        if (value == null || value == NullNode.NULL) {
            removed = this.backing.remove(key);
        } else {
            removed = this.backing.put(key, Node.fromObject(value));
        }

        return removed != null ? removed : NullNode.NULL;
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Override
    public @NotNull MapNode copy() {
        return MapNode.create(this.backing);
    }

    @Override
    public @NotNull @UnmodifiableView MapNode asView() {
        return new MapNodeImpl(Collections.unmodifiableMap(this.backing));
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
                "backing=" + this.backing +
                '}';
    }
}
