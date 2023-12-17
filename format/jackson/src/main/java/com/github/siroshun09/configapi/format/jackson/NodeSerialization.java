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

package com.github.siroshun09.configapi.format.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.siroshun09.configapi.core.node.ArrayNode;
import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
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
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * A class that provides serialization of {@link MapNode} for Jackson.
 */
public final class NodeSerialization {

    /**
     * A {@link JsonSerializer} for {@link MapNode}.
     */
    public static final JsonSerializer<MapNode> SERIALIZER = new NodeSerializer();

    /**
     * A {@link JsonDeserializer} for {@link MapNode}.
     */
    public static final JsonDeserializer<MapNode> DESERIALIZER = new NodeDeserializer();

    /**
     * Creates a new {@link SimpleModule} that has {@link #SERIALIZER} and {@link #DESERIALIZER}.
     *
     * @return a new {@link SimpleModule} that has {@link #SERIALIZER} and {@link #DESERIALIZER}
     */
    public static @NotNull SimpleModule createModule() {
        var module = new SimpleModule();
        module.addSerializer(SERIALIZER);
        module.addDeserializer(MapNode.class, DESERIALIZER);
        return module;
    }

    private static final class NodeSerializer extends JsonSerializer<MapNode> {

        private NodeSerializer() {
        }

        @Override
        public Class<MapNode> handledType() {
            return MapNode.class;
        }

        @Override
        public void serialize(MapNode value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            this.writeNode(gen, value);
        }

        private void writeNode(JsonGenerator gen, Node<?> value) throws IOException {
            if (value instanceof StringValue stringValue) {
                gen.writeString(stringValue.value());
            } else if (value instanceof EnumValue<?> enumValue) {
                gen.writeString(enumValue.value().name());
            } else if (value instanceof NumberValue numberValue) {
                var clazz = numberValue.getClass();

                if (clazz == IntValue.class) {
                    gen.writeNumber(numberValue.asInt());
                } else if (clazz == LongValue.class) {
                    gen.writeNumber(numberValue.asLong());
                } else if (clazz == ByteValue.class) {
                    gen.writeNumber(numberValue.asByte());
                } else if (clazz == ShortValue.class) {
                    gen.writeNumber(numberValue.asShort());
                } else if (clazz == FloatValue.class) {
                    gen.writeNumber(numberValue.asFloat());
                } else if (clazz == DoubleValue.class) {
                    gen.writeNumber(numberValue.asDouble());
                }
            } else if (value instanceof BooleanValue booleanValue) {
                gen.writeBoolean(booleanValue.asBoolean());
            } else if (value instanceof NullNode || value == null) {
                gen.writeNull();
            } else if (value instanceof ListNode listNode) {
                gen.writeStartArray();

                for (var element : listNode.value()) {
                    this.writeNode(gen, element);
                }

                gen.writeEndArray();
            } else if (value instanceof MapNode mapNode) {
                gen.writeStartObject();

                for (var entry : mapNode.value().entrySet()) {
                    gen.writeFieldName(String.valueOf(entry.getKey()));
                    this.writeNode(gen, entry.getValue());
                }

                gen.writeEndObject();
            } else if (value instanceof ArrayNode<?>) {
                gen.writeStartArray();

                if (value instanceof IntArray intArray) {
                    for (int val : intArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof LongArray longArray) {
                    for (long val : longArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof DoubleArray doubleArray) {
                    for (double val : doubleArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof FloatArray floatArray) {
                    for (float val : floatArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof ByteArray byteArray) {
                    for (byte val : byteArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof ShortArray shortArray) {
                    for (short val : shortArray.value()) {
                        gen.writeNumber(val);
                    }
                } else if (value instanceof BooleanArray booleanArray) {
                    for (boolean val : booleanArray.value()) {
                        gen.writeBoolean(val);
                    }
                }

                gen.writeEndArray();
            } else if (value instanceof CommentedNode<?> commentedNode) {
                this.writeNode(gen, commentedNode.node());
            } else {
                throw new IOException("Cannot serialize " + value.getClass().getName());
            }
        }
    }

    private static final class NodeDeserializer extends JsonDeserializer<MapNode> {

        private NodeDeserializer() {
        }

        @Override
        public Class<?> handledType() {
            return MapNode.class;
        }

        @Override
        public MapNode deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            var mapNode = MapNode.create();

            if (parser.currentToken() == JsonToken.START_OBJECT) {
                while (parser.nextToken() == JsonToken.FIELD_NAME) {
                    var fieldName = parser.currentName();
                    mapNode.set(parser.currentName(), this.readNode(parser, parser.nextToken()));
                }
            } else {
                throw new IOException("Unexpected token: " + parser.currentToken());
            }

            return mapNode;
        }

        private Node<?> readNode(JsonParser parser, JsonToken token) throws IOException {
            if (token == JsonToken.VALUE_STRING) {
                return StringValue.fromString(parser.getValueAsString());
            } else if (token == JsonToken.VALUE_NUMBER_INT) {
                long longValue = parser.getValueAsLong();
                int intValue = (int) longValue;

                return longValue == intValue ? new IntValue(intValue) : new LongValue(longValue);
            } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                return new DoubleValue(parser.getValueAsDouble());
            } else if (token == JsonToken.VALUE_TRUE) {
                return BooleanValue.TRUE;
            } else if (token == JsonToken.VALUE_FALSE) {
                return BooleanValue.FALSE;
            } else if (token == JsonToken.VALUE_NULL) {
                return NullNode.NULL;
            } else if (token == JsonToken.START_ARRAY) {
                var listNode = ListNode.create();

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    listNode.add(this.readNode(parser, parser.currentToken()));
                }

                return listNode;
            } else if (token == JsonToken.START_OBJECT) {
                var mapNode = MapNode.create();

                while (parser.nextToken() == JsonToken.FIELD_NAME) {
                    mapNode.set(parser.currentName(), this.readNode(parser, parser.nextToken()));
                }

                return mapNode;
            } else {
                throw new IOException("Unexpected token: " + token);
            }
        }
    }
}
