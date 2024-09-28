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

package dev.siroshun.configapi.format.gson;

import dev.siroshun.configapi.core.node.ArrayNode;
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
import dev.siroshun.configapi.core.node.NumberValue;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringValue;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

final class NodeAdapter extends TypeAdapter<Node<?>> {

    static final TypeAdapter<Node<?>> INSTANCE = new NodeAdapter();

    static final TypeAdapter<ListNode> LIST_NODE_ADAPTER = new TypeAdapter<>() {
        @Override
        public ListNode read(JsonReader in) throws IOException {
            var listNode = ListNode.create();

            var token = in.peek();

            if (token == JsonToken.BEGIN_ARRAY) {
                in.beginArray();

                while (in.hasNext()) {
                    listNode.add(NodeAdapter.INSTANCE.read(in));
                }

                in.endArray();

                return listNode;
            } else {
                throw new IOException("Unexpected token: " + token);
            }
        }

        @Override
        public void write(JsonWriter out, ListNode value) throws IOException {
            out.beginArray();

            for (var element : value.value()) {
                NodeAdapter.INSTANCE.write(out, element);
            }

            out.endArray();
        }
    };

    static final TypeAdapter<MapNode> MAP_NODE_ADAPTER = new TypeAdapter<>() {

        @Override
        public MapNode read(JsonReader in) throws IOException {
            var mapNode = MapNode.create();

            var token = in.peek();

            if (token == JsonToken.BEGIN_OBJECT) {
                in.beginObject();

                while (in.hasNext()) {
                    mapNode.set(in.nextName(), NodeAdapter.INSTANCE.read(in));
                }

                in.endObject();
            } else {
                throw new IOException("Unexpected token: " + token);
            }

            return mapNode;
        }

        @Override
        public void write(JsonWriter out, MapNode value) throws IOException {
            out.beginObject();

            for (var entry : value.value().entrySet()) {
                out.name(String.valueOf(entry.getKey()));
                NodeAdapter.INSTANCE.write(out, entry.getValue());
            }

            out.endObject();
        }
    };

    private NodeAdapter() {
    }

    @Override
    public Node<?> read(JsonReader in) throws IOException {
        var token = in.peek();

        if (token == JsonToken.STRING) {
            return StringValue.fromString(in.nextString());
        } else if (token == JsonToken.NUMBER) {
            return readNumber(in);
        } else if (token == JsonToken.BOOLEAN) {
            return BooleanValue.fromBoolean(in.nextBoolean());
        } else if (token == JsonToken.NULL) {
            return NullNode.NULL;
        } else if (token == JsonToken.BEGIN_ARRAY) {
            return LIST_NODE_ADAPTER.read(in);
        } else if (token == JsonToken.BEGIN_OBJECT) {
            return MAP_NODE_ADAPTER.read(in);
        } else {
            throw new IOException("Unexpected token: " + token);
        }
    }

    private NumberValue readNumber(final JsonReader in) throws IOException {
        final String number = in.nextString();

        if (number.contains(".")) {
            return new DoubleValue(Double.parseDouble(number));
        }

        long longValue = Long.parseLong(number);
        int intValue = (int) longValue;

        return longValue == intValue ? new IntValue(intValue) : new LongValue(longValue);
    }

    @Override
    public void write(JsonWriter out, Node<?> value) throws IOException {
        if (value instanceof StringValue stringValue) {
            out.value(stringValue.value());
        } else if (value instanceof EnumValue<?> enumValue) {
            out.value(enumValue.value().name());
        } else if (value instanceof NumberValue numberValue) {
            var clazz = numberValue.getClass();

            if (clazz == IntValue.class || clazz == LongValue.class || clazz == ByteValue.class || clazz == ShortValue.class) {
                out.value(numberValue.asLong());
            } else if (clazz == FloatValue.class) {
                out.value(numberValue.asFloat());
            } else if (clazz == DoubleValue.class) {
                out.value(numberValue.asDouble());
            }
        } else if (value instanceof BooleanValue booleanValue) {
            out.value(booleanValue.value());
        } else if (value instanceof CharValue charValue) {
            out.value(charValue.asString());
        } else if (value instanceof NullNode || value == null) {
            out.nullValue();
        } else if (value instanceof ListNode listNode) {
            LIST_NODE_ADAPTER.write(out, listNode);
        } else if (value instanceof MapNode mapNode) {
            MAP_NODE_ADAPTER.write(out, mapNode);
        } else if (value instanceof ArrayNode<?>) {
            out.beginArray();

            if (value instanceof IntArray intArray) {
                for (int val : intArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof LongArray longArray) {
                for (long val : longArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof DoubleArray doubleArray) {
                for (double val : doubleArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof FloatArray floatArray) {
                for (float val : floatArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof ByteArray byteArray) {
                for (byte val : byteArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof ShortArray shortArray) {
                for (short val : shortArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof BooleanArray booleanArray) {
                for (boolean val : booleanArray.value()) {
                    out.value(val);
                }
            } else if (value instanceof CharArray charArray) {
                for (char val : charArray.value()) {
                    out.value(String.valueOf(val));
                }
            }

            out.endArray();
        } else if (value instanceof CommentedNode<?> commentedNode) {
            write(out, commentedNode.node());
        } else {
            throw new IOException("Cannot serialize " + value.getClass().getName());
        }
    }
}
