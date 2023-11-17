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

package com.github.siroshun09.configapi.format.gson;

import com.github.siroshun09.configapi.core.node.ArrayNode;
import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
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
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

final class NodeSerializer extends TypeAdapter<MapNode> {

    static final NodeSerializer INSTANCE = new NodeSerializer();

    private NodeSerializer() {
    }

    @Override
    public MapNode read(JsonReader in) throws IOException {
        var mapNode = MapNode.create();

        var token = in.peek();

        if (token == JsonToken.BEGIN_OBJECT) {
            in.beginObject();

            while (in.hasNext()) {
                mapNode.set(in.nextName(), this.readNode(in));
            }

            in.endObject();
        } else {
            throw new IOException("Unexpected token: " + token);
        }

        return mapNode;
    }

    private Node<?> readNode(JsonReader in) throws IOException {
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
            in.beginArray();
            var listNode = ListNode.create();

            while (in.hasNext()) {
                listNode.add(this.readNode(in));
            }

            in.endArray();

            return listNode;
        } else if (token == JsonToken.BEGIN_OBJECT) {
            in.beginObject();
            var mapNode = MapNode.create();

            while (in.hasNext()) {
                mapNode.set(in.nextName(), this.readNode(in));
            }

            in.endObject();
            return mapNode;
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
    public void write(JsonWriter out, MapNode value) throws IOException {
        writeNode(out, value);
    }

    private void writeNode(JsonWriter out, Node<?> value) throws IOException {
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
        } else if (value instanceof NullNode || value == null) {
            out.nullValue();
        } else if (value instanceof ListNode listNode) {
            out.beginArray();

            for (var element : listNode.value()) {
                writeNode(out, element);
            }

            out.endArray();
        } else if (value instanceof MapNode mapNode) {
            out.beginObject();

            for (var entry : mapNode.value().entrySet()) {
                out.name(String.valueOf(entry.getKey()));
                writeNode(out, entry.getValue());
            }

            out.endObject();
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
            }

            out.endArray();
        } else {
            throw new IOException("Cannot serialize " + value.getClass().getName());
        }
    }
}
