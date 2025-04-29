/*
 *     Copyright 2025 Siroshun09
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
            return switch (value) {
                case ValueNode<?> valueNode -> valueNode;
                case ListNode listNode -> listNode.copy();
                case MapNode mapNode -> mapNode.copy();
                case CommentedNode<?> commentedNode ->
                        CommentableNode.withComment(toNode(commentedNode.node()), commentedNode.getCommentOrNull());
                default -> toNode(((Node<?>) value).value());
            };
        }

        if (value.getClass().isArray()) {
            return fromArray(value);
        }

        return switch (value) {
            case String string -> StringValue.fromString(string);
            case Number number -> NumberValue.fromNumber(number);
            case Boolean bool -> BooleanValue.fromBoolean(bool);
            case Character charValue -> new CharValue(charValue);
            case Enum enumValue -> new EnumValue<>(enumValue);
            case Collection<?> collection -> ListNode.create(collection);
            case Map<?, ?> map -> MapNode.create(map);
            default -> new ObjectNode<>(value);
        };
    }

    private static @NotNull Node<?> fromArray(@NotNull Object value) {
        return switch (value) {
            case int[] array -> new IntArray(array);
            case long[] array -> new LongArray(array);
            case float[] array -> new FloatArray(array);
            case double[] array -> new DoubleArray(array);
            case byte[] array -> new ByteArray(array);
            case short[] array -> new ShortArray(array);
            case boolean[] array -> new BooleanArray(array);
            case char[] array -> new CharArray(array);
            case Object[] array -> ListNode.create(Arrays.asList(array));
            default -> throw new IllegalArgumentException("unexpected array: " + value);
        };
    }

    private NodeUtils() {
        throw new UnsupportedOperationException();
    }
}
