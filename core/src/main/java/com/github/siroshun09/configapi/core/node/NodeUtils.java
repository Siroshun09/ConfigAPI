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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

final class NodeUtils {

    @SuppressWarnings({"rawtypes", "unchecked"})
    static @NotNull Node<?> toNode(@Nullable Object value) {
        if (value == null || value == NullNode.NULL) {
            return NullNode.NULL;
        }

        if (value instanceof Node<?>) {
            if (value instanceof ValueNode<?> valueNode) {
                return valueNode;
            } else if (value instanceof ListNode listNode) {
                return listNode.copy();
            } else if (value instanceof MapNode mapNode) {
                return mapNode.copy();
            } else if (value instanceof CommentedNode commentedNode) {
                return CommentableNode.withComment(toNode(commentedNode.node()), commentedNode.getCommentOrNull());
            } else {
                return toNode(((Node<?>) value).value());
            }
        }

        if (value.getClass().isArray()) {
            return fromArray(value);
        }

        if (value instanceof String string) {
            return StringValue.fromString(string);
        } else if (value instanceof Number number) {
            return NumberValue.fromNumber(number);
        } else if (value instanceof Boolean bool) {
            return BooleanValue.fromBoolean(bool);
        } else if (value instanceof Enum enumValue) {
            return new EnumValue<>(enumValue);
        } else if (value instanceof Collection<?> collection) {
            return ListNode.create(collection);
        } else if (value instanceof Map<?, ?> map) {
            return MapNode.create(map);
        } else {
            return new ObjectNode<>(value);
        }
    }

    private static @NotNull Node<?> fromArray(@NotNull Object value) {
        if (value instanceof int[] array) {
            return new IntArray(array);
        } else if (value instanceof long[] array) {
            return new LongArray(array);
        } else if (value instanceof float[] array) {
            return new FloatArray(array);
        } else if (value instanceof double[] array) {
            return new DoubleArray(array);
        } else if (value instanceof byte[] array) {
            return new ByteArray(array);
        } else if (value instanceof short[] array) {
            return new ShortArray(array);
        } else if (value instanceof boolean[] array) {
            return new BooleanArray(array);
        } else if (value instanceof Object[] array) {
            return ListNode.create(Arrays.asList(array));
        } else {
            throw new IllegalArgumentException("unexpected array: " + value);
        }
    }

    private NodeUtils() {
        throw new UnsupportedOperationException();
    }
}
