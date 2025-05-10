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

package dev.siroshun.configapi.core.node.visitor;

import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.ByteValue;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.DoubleArray;
import dev.siroshun.configapi.core.node.DoubleValue;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.FloatArray;
import dev.siroshun.configapi.core.node.FloatValue;
import dev.siroshun.configapi.core.node.IntArray;
import dev.siroshun.configapi.core.node.IntValue;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.LongArray;
import dev.siroshun.configapi.core.node.LongValue;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NullNode;
import dev.siroshun.configapi.core.node.ObjectNode;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link NodeVisitor} that represents {@link Node}s as {@link String}.
 */
@NotNullByDefault
public final class StringNodeVisitor implements NodeVisitor {

    private static final Appender<String> DEFAULT_ESCAPING_STRING_APPENDER = StringNodeVisitor::appendQuoteAndEscapedString;
    private static final Appender<Object> DEFAULT_OBJECT_APPENDER = (obj, builder) -> appendQuoteAndEscapedString(String.valueOf(obj), builder);

    /**
     * Appends the quoted/escaped {@link String} to {@link StringBuilder}.
     *
     * @param str     the {@link String} to append
     * @param builder a {@link StringBuilder}
     */
    public static void appendQuoteAndEscapedString(String str, StringBuilder builder) {
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
    public static StringNodeVisitor create() {
        return new StringNodeVisitor(new StringBuilder(), DEFAULT_ESCAPING_STRING_APPENDER, DEFAULT_OBJECT_APPENDER);
    }

    /**
     * Creates a new {@link StringNodeVisitor.Builder}.
     *
     * @return a new {@link StringNodeVisitor.Builder}
     */
    @Contract(value = " -> new", pure = true)
    public static StringNodeVisitor.Builder builder() {
        return new Builder();
    }

    private final StringBuilder builder;
    private final Appender<String> stringAppender;
    private final Appender<Object> objecctAppender;

    private StringNodeVisitor(StringBuilder builder, Appender<String> stringAppender, Appender<Object> objecctAppender) {
        this.builder = builder;
        this.stringAppender = stringAppender;
        this.objecctAppender = objecctAppender;
    }

    @Override
    public VisitResult visit(StringValue value) {
        this.stringAppender.append(value.asString(), this.builder);
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(BooleanArray array) {
        boolean[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(BooleanValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(ByteArray array) {
        byte[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(ByteValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(CharArray array) {
        char[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(CharValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(DoubleArray array) {
        double[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(DoubleValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(FloatArray array) {
        float[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(FloatValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(IntArray array) {
        int[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(IntValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(LongArray array) {
        long[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(LongValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(ShortArray array) {
        short[] arr = array.value();
        this.appendArray(arr.length, (i, builder) -> builder.append(arr[i]));
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(ShortValue value) {
        this.builder.append(value.value());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(EnumValue<?> value) {
        this.builder.append(value.value().name());
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(ObjectNode<?> node) {
        this.objecctAppender.append(node.value(), this.builder);
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult startList(ListNode node) {
        this.builder.append('[');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visitElement(int index, Node<?> node) {
        if (index != 0) this.builder.append(',');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult endList(ListNode node) {
        this.builder.append(']');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult startMap(MapNode node) {
        this.builder.append('{');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visitEntry(int num, Object key, Node<?> node) {
        if (num != 0) {
            this.builder.append(',');
        }
        if (key instanceof Node<?> keyNode) {
            keyNode.accept(this);
        } else {
            this.objecctAppender.append(key, this.builder);
        }
        this.builder.append('=');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult endMap(MapNode node) {
        this.builder.append('}');
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(CommentedNode<?> node) {
        return VisitResult.CONTINUE;
    }

    @Override
    public VisitResult visit(NullNode node) {
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
    public String toString() {
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
        void append(T obj, StringBuilder builder);

    }

    @FunctionalInterface
    private interface ElementAppender {
        void append(int index, StringBuilder builder);
    }

    /**
     * A builder class of {@link StringNodeVisitor}.
     */
    public static final class Builder {

        private @Nullable StringBuilder builder;
        private @Nullable Appender<String> stringAppender;
        private @Nullable Appender<Object> objectAppender;

        private Builder() {
        }

        /**
         * Sets a {@link StringBuilder} to append string-represented nodes.
         *
         * @param builder a {@link StringBuilder}
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public Builder setStringBuilder(StringBuilder builder) {
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
        public Builder setStringAppender(Appender<String> stringAppender) {
            this.stringAppender = stringAppender;
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
        public Builder setObjectAppender(Appender<Object> objectAppender) {
            this.objectAppender = objectAppender;
            return this;
        }

        /**
         * Creates a new {@link StringBuilder}.
         *
         * @return a new {@link StringBuilder}
         */
        @Contract("-> new")
        public StringNodeVisitor build() {
            return new StringNodeVisitor(
                    this.builder != null ? this.builder : new StringBuilder(),
                    this.stringAppender != null ? this.stringAppender : DEFAULT_ESCAPING_STRING_APPENDER,
                    this.objectAppender != null ? this.objectAppender : DEFAULT_OBJECT_APPENDER
            );
        }
    }
}
