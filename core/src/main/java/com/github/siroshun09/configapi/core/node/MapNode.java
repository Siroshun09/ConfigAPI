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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Node} implementation that represents a {@link Map} ({@link Object} - {@link Node}).
 * <p>
 * The implementing class of this interface keeps the order of keys using {@link LinkedHashMap}.
 */
public sealed interface MapNode extends CommentableNode<Map<Object, Node<?>>> permits MapNodeImpl {

    /**
     * An implementation {@link Class} of this interface.
     */
    @ApiStatus.Internal
    Class<? extends MapNode> IMPLEMENTATION_CLASS = MapNodeImpl.class;

    /**
     * Creates a new {@link MapNode}.
     *
     * @return a new {@link MapNode}
     */
    static @NotNull MapNode create() {
        return new MapNodeImpl(new LinkedHashMap<>(), false, new AtomicReference<>());
    }

    /**
     * Creates a new {@link MapNode} with entries in the given {@link Map}.
     *
     * @param map a {@link Map} to add entries to the new {@link MapNode}
     * @return a new {@link MapNode} with entries in the given {@link Map}
     */
    static @NotNull MapNode create(@NotNull Map<?, ?> map) {
        Objects.requireNonNull(map);

        var converted = new LinkedHashMap<Object, Node<?>>(map.size(), 1.0f);

        for (var entry : map.entrySet()) {
            converted.put(entry.getKey(), Node.fromObject(entry.getValue()));
        }

        return new MapNodeImpl(converted, false, new AtomicReference<>());
    }

    /**
     * Gets a {@link MapNode} that is always empty.
     * <p>
     * The returning {@link MapNode} cannot be modified using methods like {@link #set(Object, Object)}.
     *
     * @return a {@link MapNode} that is always empty
     */
    static @NotNull MapNode empty() {
        return MapNodeImpl.EMPTY;
    }

    @Override
    @UnknownNullability @UnmodifiableView Map<Object, Node<?>> value();

    /**
     * Gets a {@link Node} to which the specified key is mapped,
     * or {@link NullNode#NULL} if this {@link MapNode} contains no mapping for the key.
     *
     * @param key the key to get
     * @return a {@link Node} to which the specified key is mapped, or {@link NullNode#NULL} if this {@link MapNode} contains no mapping for the key
     */
    default @NotNull Node<?> get(@NotNull Object key) {
        return this.getOrDefault(key, NullNode.NULL);
    }

    /**
     * Gets a {@link Node} to which the specified key is mapped,
     * or the specified {@link Node} if this {@link MapNode} contains no mapping for the key.
     *
     * @param key         the key to get
     * @param defaultNode the {@link Node} to return when this {@link MapNode} contains no mapping for the key
     * @return a {@link Node} to which the specified key is mapped, or the specified {@link Node} if this {@link MapNode} contains no mapping for the key
     */
    @NotNull Node<?> getOrDefault(@NotNull Object key, @NotNull Node<?> defaultNode);

    /**
     * Sets a {@link Node} to the specified key.
     * <p>
     * If {@code null} is specified as value, the key will be removed from the map.
     *
     * @param key   the key to set
     * @param value the value to set, or {@code null} if removing the key
     * @return the {@link Node} to which the specified key is mapped previously, or {@link NullNode#NULL} if the key is not mapped
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull Node<?> set(@NotNull Object key, @Nullable Object value);

    /**
     * Sets a {@link Node} if this {@link MapNode} does not contain the specified key.
     *
     * @param key   the key to set
     * @param value the value to set
     * @return the {@link Node} to which the specified key is mapped previously, or {@code null} if the key is not mapped
     */
    @Nullable Node<?> setIfAbsent(@NotNull Object key, @NotNull Object value);

    /**
     * Clears this {@link MapNode}.
     */
    void clear();

    /**
     * Copies this {@link MapNode}.
     * <p>
     * The entries in this {@link MapNode} will also be copied using {@link Node#fromObject(Object)}.
     *
     * @return a copied {@link MapNode}
     */
    @NotNull MapNode copy();

    /**
     * Gets a view of this {@link MapNode}.
     * <p>
     * The returning {@link MapNode} cannot be modified, but this {@link MapNode} can still be modified,
     * so the entries may be changed by other codes using this instance.
     *
     * @return a view of this {@link MapNode}
     */
    @NotNull @UnmodifiableView MapNode asView();

    /* --- Helper Methods --- */

    /**
     * Gets the {@link ListNode} to which the specified key is mapped.
     * <p>
     * The returning {@link ListNode} is a view, so it cannot be modified. To modify {@link ListNode}, use {@link #getOrCreateList(Object)}.
     *
     * @param key the key to get
     * @return the view of {@link ListNode} to which the specified key is mapped, or {@link ListNode#empty()} if the key is not mapped
     */
    default @NotNull @UnmodifiableView ListNode getList(@NotNull Object key) {
        return this.raw(key) instanceof ListNode listNode ? listNode.asView() : ListNode.empty();
    }

    /**
     * Creates a new {@link ListNode} and set it to the specified key.
     * <p>
     * The {@link Node} to which the specified key is mapped previously will be removed.
     *
     * @param key the key to set
     * @return a new {@link ListNode} to which the specified key is mapped
     */
    @NotNull ListNode createList(@NotNull Object key);

    /**
     * Gets the {@link ListNode} to which the specified key is mapped, or creates a new {@link ListNode} using {@link #createList(Object)}.
     *
     * @param key the key to get
     * @return the {@link ListNode} to which the specified key is mapped, or {@link #createList(Object)} if the key is not mapped
     */
    default @NotNull ListNode getOrCreateList(@NotNull Object key) {
        return this.raw(key) instanceof ListNode listNode ? listNode : this.createList(key);
    }

    /**
     * Gets the {@link MapNode} to which the specified key is mapped.
     * <p>
     * The returning {@link MapNode} is a view, so it cannot be modified. To modify {@link MapNode}, use {@link #getOrCreateMap(Object)}.
     *
     * @param key the key to get
     * @return the view of {@link MapNode} to which the specified key is mapped, or {@link MapNode#empty()} if the key is not mapped to {@link MapNode}
     */
    default @NotNull @Unmodifiable MapNode getMap(@NotNull Object key) {
        return this.raw(key) instanceof MapNode mapNode ? mapNode.asView() : empty();
    }

    /**
     * Creates a new {@link MapNode} and set it to the specified key.
     * <p>
     * The {@link Node} to which the specified key is mapped previously will be removed.
     *
     * @param key the key to set
     * @return a new {@link MapNode} to which the specified key is mapped
     */
    @NotNull @Unmodifiable MapNode createMap(@NotNull Object key);

    /**
     * Gets the {@link MapNode} to which the specified key is mapped, or creates a new {@link MapNode} using {@link #createMap(Object)}.
     *
     * @param key the key to get
     * @return the {@link MapNode} to which the specified key is mapped, or {@link #createMap(Object)} if the key is not mapped to {@link MapNode}
     */
    default @NotNull MapNode getOrCreateMap(@NotNull Object key) {
        return this.raw(key) instanceof MapNode mapNode ? mapNode : this.createMap(key);
    }

    /**
     * Gets the {@link String} value to which the specified key is mapped, or an empty {@link String}.
     *
     * @param key the key to get
     * @return the {@link String} to which the specified key is mapped, or an empty {@link String} if the key is not mapped to {@link StringValue}
     */
    default @NotNull String getString(@NotNull Object key) {
        return this.getString(key, "");
    }

    /**
     * Gets the {@link String} value to which the specified key is mapped, or the specified value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the {@link String} to which the specified key is mapped, or the specified value if the key is not mapped to {@link StringValue}
     */
    default @NotNull String getString(@NotNull Object key, @NotNull String def) {
        return this.raw(key) instanceof StringValue value ? value.asString() : def;
    }

    /**
     * Gets the {@link String} value to which the specified key is mapped, or {@code null}.
     *
     * @param key the key to get
     * @return the {@link String} to which the specified key is mapped, or {@code null} if the key is not mapped to {@link StringValue}
     */
    default @Nullable String getStringOrNull(@NotNull Object key) {
        return this.raw(key) instanceof StringValue value ? value.asString() : null;
    }

    /**
     * Gets the {@link Enum} value or parses {@link StringValue} to {@link Enum}.
     * <p>
     * This method is implemented with the following specification:
     *
     * <ul>
     *     <li>The {@link Node} to which the specified key is mapped is {@link EnumValue} and it is appropriate the specified type of the enum, returns {@link EnumValue#value()}</li>
     *     <li>
     *         The {@link Node} to which the specified key is mapped is {@link StringValue}, try to parse the string to {@link Enum} using {@link Enum#valueOf(Class, String)}
     *     <ul>
     *         <li>If the string value is invalid, returns the specified value</li>
     *     </ul>
     *     </li>
     *     <li>Otherwise, returns the specified value</li>
     * </ul>
     *
     * @param key the key to get
     * @param def the default value
     * @param <E> the type of the {@link Enum}
     * @return the {@link Enum} value
     */
    @SuppressWarnings("unchecked")
    default <E extends Enum<E>> @NotNull E getEnum(@NotNull Object key, @NotNull E def) {
        var value = this.getEnum(key, def.getClass());
        return value != null ? (E) value : def;
    }

    /**
     * Gets the {@link Enum} value or parses {@link StringValue} to {@link Enum}.
     * <p>
     * This method is implemented with the following specification:
     *
     * <ul>
     *     <li>The {@link Node} to which the specified key is mapped is {@link EnumValue} and it is appropriate the specified type of the enum, returns {@link EnumValue#value()}</li>
     *     <li>
     *         The {@link Node} to which the specified key is mapped is {@link StringValue}, try to parse the string to {@link Enum} using {@link Enum#valueOf(Class, String)}
     *     <ul>
     *         <li>If the string value is invalid, returns {@code null}</li>
     *     </ul>
     *     </li>
     *     <li>Otherwise, returns {@code null}</li>
     * </ul>
     *
     * @param key       the key to get
     * @param enumClass the {@link Class} of the {@link Enum}
     * @param <E>       the type of the {@link Enum}
     * @return the {@link Enum} value or {@code null}
     */
    default <E extends Enum<E>> @Nullable E getEnum(@NotNull Object key, @NotNull Class<E> enumClass) {
        var node = this.raw(key);

        if (node instanceof EnumValue<?> enumValue) {
            return enumClass.isInstance(enumValue.value()) ? enumClass.cast(enumValue.value()) : null;
        }

        if (node instanceof StringValue stringValue) {
            try {
                return Enum.valueOf(enumClass, stringValue.value());
            } catch (IllegalArgumentException ignored1) {
                try {
                    return Enum.valueOf(enumClass, stringValue.value().toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException ignored2) {
                }
            }
        }

        return null;
    }

    /**
     * Gets the boolean value to which the specified key is mapped, or {@code false}.
     *
     * @param key the key to get
     * @return the boolean value to which the specified key is mapped, or {@code false} if the key is not mapped to {@link BooleanValue}
     */
    default boolean getBoolean(@NotNull Object key) {
        return this.getBoolean(key, false);
    }

    /**
     * Gets the boolean value to which the specified key is mapped, or the specified boolean value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the boolean value to which the specified key is mapped, or the specified value if the key is not mapped to {@link BooleanValue}
     */
    default boolean getBoolean(@NotNull Object key, boolean def) {
        return this.raw(key) instanceof BooleanValue booleanValue ? booleanValue.asBoolean() : def;
    }

    /**
     * Gets the int value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the int value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default int getInteger(@NotNull Object key) {
        return this.getInteger(key, 0);
    }

    /**
     * Gets the int value to which the specified key is mapped, or the specified int value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the int value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default int getInteger(@NotNull Object key, int def) {
        return this.raw(key) instanceof NumberValue value ? value.asInt() : def;
    }

    /**
     * Gets the long value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the long value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default long getLong(@NotNull Object key) {
        return this.getLong(key, 0L);
    }

    /**
     * Gets the long value to which the specified key is mapped, or the specified long value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the long value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default long getLong(@NotNull Object key, long def) {
        return this.raw(key) instanceof NumberValue value ? value.asLong() : def;
    }

    /**
     * Gets the float value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the float value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default float getFloat(@NotNull Object key) {
        return this.getFloat(key, 0.0f);
    }

    /**
     * Gets the float value to which the specified key is mapped, or the specified float value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the float value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default float getFloat(@NotNull Object key, float def) {
        return this.raw(key) instanceof NumberValue value ? value.asFloat() : def;
    }

    /**
     * Gets the double value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the double value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default double getDouble(@NotNull Object key) {
        return this.getDouble(key, 0.0);
    }

    /**
     * Gets the double value to which the specified key is mapped, or the specified double value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the double value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default double getDouble(@NotNull Object key, double def) {
        return this.raw(key) instanceof NumberValue value ? value.asDouble() : def;
    }

    /**
     * Gets the byte value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the byte value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default byte getByte(@NotNull Object key) {
        return this.getByte(key, (byte) 0);
    }

    /**
     * Gets the byte value to which the specified key is mapped, or the specified byte value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the byte value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default byte getByte(@NotNull Object key, byte def) {
        return this.raw(key) instanceof NumberValue value ? value.asByte() : def;
    }

    /**
     * Gets the short value to which the specified key is mapped, or {@code 0}.
     *
     * @param key the key to get
     * @return the short value to which the specified key is mapped, or {@code 0} if the key is not mapped to {@link NumberValue}
     */
    default short getShort(@NotNull Object key) {
        return this.getShort(key, (short) 0);
    }

    /**
     * Gets the short value to which the specified key is mapped, or the specified short value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the short value to which the specified key is mapped, or the specified value if the key is not mapped to {@link NumberValue}
     */
    default short getShort(@NotNull Object key, short def) {
        return this.raw(key) instanceof NumberValue value ? value.asShort() : def;
    }

    /**
     * Gets the char value to which the specified key is mapped, or {@code false}.
     *
     * @param key the key to get
     * @return the char value to which the specified key is mapped, or {@link Character#MIN_VALUE} if the key is not mapped to {@link CharValue}
     */
    default char getChar(@NotNull Object key) {
        return this.getChar(key, Character.MIN_VALUE);
    }

    /**
     * Gets the char value to which the specified key is mapped, or the specified char value.
     *
     * @param key the key to get
     * @param def the default value
     * @return the char value to which the specified key is mapped, or the specified value if the key is not mapped to {@link CharValue}
     */
    default char getChar(@NotNull Object key, char def) {
        return this.raw(key) instanceof CharValue charValue ? charValue.asChar() : def;
    }

    private @NotNull Node<?> raw(@NotNull Object key) {
        var node = this.get(key);
        return node instanceof CommentedNode<?> commentedNode ? commentedNode.node() : node;
    }
}
