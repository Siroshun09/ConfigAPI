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

package com.github.siroshun09.configapi.core.node.visitor;

import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CharArray;
import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.CommentedNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.FloatValue;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.IntValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.LongValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.github.siroshun09.configapi.core.node.ObjectNode;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link NodeVisitor} that represents {@link Node}s as {@link String}.
 */
public final class StringNodeVisitor implements NodeVisitor {

    private static final Appender<String> DEFAULT_ESCAPING_STRING_APPENDER = StringNodeVisitor::appendQuoteAndEscapedString;

    private static final Appender<Object> DEFAULT_KEY_APPENDER = (obj, builder) -> DEFAULT_ESCAPING_STRING_APPENDER.append(String.valueOf(obj), builder);

    private static final Appender<Object> DEFAULT_OBJECT_APPENDER = (obj, builder) -> builder.append(obj);

    /**
     * Appends the quoted/escaped {@link String} to {@link StringBuilder}.
     *
     * @param str     the {@link String} to append
     * @param builder a {@link StringBuilder}
     */
    public static void appendQuoteAndEscapedString(@NotNull String str, @NotNull StringBuilder builder) {
        if (str.isEmpty()) {
            builder.append('"').append('"');
            return;
        }

        int firstIndex = builder.length();
        boolean quote = false;

        for (int i = 0, l = str.length(); i < l; i++) {
            char c = str.charAt(i);

            if (c == '\\') {
                builder.append('\\');
            } else if (c == '"') {
                quote = true;
                builder.append('\\');
            } else if (c == ' ' || c == '\'') {
                quote = true;
            }

            builder.append(c);
        }

        if (quote) {
            builder.insert(firstIndex, '"');
            builder.append('"');
        }
    }

    /**
     * Creates a new {@link StringNodeVisitor}.
     *
     * @return a new {@link StringNodeVisitor}
     */
    @Contract(" -> new")
    public static @NotNull StringNodeVisitor create() {
        return new StringNodeVisitor(new StringBuilder(), DEFAULT_ESCAPING_STRING_APPENDER, DEFAULT_KEY_APPENDER, DEFAULT_OBJECT_APPENDER);
    }

    /**
     * Creates a new {@link StringNodeVisitor.Builder}.
     *
     * @return a new {@link StringNodeVisitor.Builder}
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull StringNodeVisitor.Builder builder() {
        return new Builder();
    }

    private final StringBuilder builder;
    private final Appender<String> stringAppender;
    private final Appender<Object> keyAppender;
    private final Appender<Object> objecctAppender;

    private StringNodeVisitor(StringBuilder builder, Appender<String> stringAppender,
                              Appender<Object> keyAppender, Appender<Object> objecctAppender) {
        this.builder = builder;
        this.stringAppender = stringAppender;
        this.keyAppender = keyAppender;
        this.objecctAppender = objecctAppender;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull StringValue value) {
        this.stringAppender.append(value.asString(), this.builder);
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull BooleanArray array) {
        boolean[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull BooleanValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull ByteArray array) {
        byte[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull ByteValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull CharArray array) {
        char[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull CharValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull DoubleArray array) {
        double[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull DoubleValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull FloatArray array) {
        float[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull FloatValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull IntArray array) {
        int[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull IntValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull LongArray array) {
        long[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull LongValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull ShortArray array) {
        short[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull ShortValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull EnumValue<?> value) {
        this.builder.append(value.value().name());
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull ObjectNode<?> node) {
        this.objecctAppender.append(node.value(), this.builder);
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult startList(@NotNull ListNode node) {
        this.builder.append('[');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visitElement(int index, @NotNull Node<?> node) {
        if (index != 0) this.builder.append(',');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult endList(@NotNull ListNode node) {
        this.builder.append(']');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult startMap(@NotNull MapNode node) {
        this.builder.append('{');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visitEntry(int num, @NotNull Object key, @NotNull Node<?> node) {
        if (num != 0) {
            this.builder.append(',');
        }
        if (key instanceof Node<?> keyNode) {
            keyNode.accept(this);
        } else {
            this.keyAppender.append(key, this.builder);
        }
        this.builder.append('=');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult endMap(@NotNull MapNode node) {
        this.builder.append('}');
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull CommentedNode<?> node) {
        return VisitResult.CONTINUE;
    }

    @Override
    public @NotNull VisitResult visit(@NotNull NullNode node) {
        this.builder.append("null");
        return VisitResult.CONTINUE;
    }

    /**
     * Gets the result of string representation of {@link Node}s.
     *
     * @return the result of string representation of {@link Node}s
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return this.builder.toString();
    }

    private void appendArray(int size, ElementAppender appender) {
        if (size == 0) {
            this.builder.append("[]");
        } else {
            this.builder.append('[');

            for (int i = 0; i < size; i++) {
                if (i != 0) this.builder.append(',');
                appender.append(i, this.builder);
            }

            this.builder.append(']');
        }
    }

    /**
     * An interface to appends an object to {@link StringBuilder}.
     *
     * @param <T> the object type
     */
    public interface Appender<T> {

        /**
         * Appends an object to {@link StringBuilder}.
         *
         * @param obj     an object
         * @param builder a {@link StringBuilder}
         */
        void append(@NotNull T obj, @NotNull StringBuilder builder);

    }

    private interface ElementAppender {
        void append(int index, @NotNull StringBuilder builder);
    }

    /**
     * A builder class of {@link StringNodeVisitor}.
     */
    public static final class Builder {

        private StringBuilder builder;
        private Appender<String> stringAppender;
        private Appender<Object> keyAppender;
        private Appender<Object> objectAppender;

        private Builder() {
        }

        /**
         * Sets a {@link StringBuilder} to append string-represented nodes.
         *
         * @param builder a {@link StringBuilder}
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder setStringBuilder(StringBuilder builder) {
            this.builder = builder;
            return this;
        }

        /**
         * Sets a {@link Appender} for {@link StringValue}s.
         * <p>
         * This {@link Appender} will be used in {@link StringNodeVisitor#visit(StringValue)}.
         *
         * @param stringAppender a {@link Appender} for {@link StringValue}s
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder setStringAppender(Appender<String> stringAppender) {
            this.stringAppender = stringAppender;
            return this;
        }

        /**
         * Sets a {@link Appender} for {@link MapNode}'s keys.
         * <p>
         * This {@link Appender} will be used in {@link StringNodeVisitor#visitEntry(int, Object, Node)}.
         *
         * @param keyAppender a {@link Appender} for {@link MapNode}'s keys
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder setKeyAppender(Appender<Object> keyAppender) {
            this.keyAppender = keyAppender;
            return this;
        }

        /**
         * Sets a {@link Appender} for {@link ObjectNode}s.
         * <p>
         * This {@link Appender} will be used in {@link StringNodeVisitor#visit(ObjectNode)}.
         *
         * @param objectAppender a {@link Appender} for {@link ObjectNode}s
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder setObjectAppender(Appender<Object> objectAppender) {
            this.objectAppender = objectAppender;
            return this;
        }

        /**
         * Creates a new {@link StringBuilder}.
         *
         * @return a new {@link StringBuilder}
         */
        @Contract("-> new")
        public @NotNull StringNodeVisitor build() {
            return new StringNodeVisitor(
                    this.builder != null ? this.builder : new StringBuilder(),
                    this.stringAppender != null ? this.stringAppender : DEFAULT_ESCAPING_STRING_APPENDER,
                    this.keyAppender != null ? this.keyAppender : DEFAULT_KEY_APPENDER,
                    this.objectAppender != null ? this.objectAppender : DEFAULT_OBJECT_APPENDER
            );
        }
    }
}
