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

package dev.siroshun.configapi.core.serialization.record;

import dev.siroshun.configapi.core.comment.SimpleComment;
import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.CommentableNode;
import dev.siroshun.configapi.core.node.DoubleArray;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.FloatArray;
import dev.siroshun.configapi.core.node.IntArray;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.LongArray;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NullNode;
import dev.siroshun.configapi.core.node.NumberValue;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.core.serialization.SerializationException;
import dev.siroshun.configapi.core.serialization.Serializer;
import dev.siroshun.configapi.core.serialization.annotation.Comment;
import dev.siroshun.configapi.core.serialization.annotation.Inline;
import dev.siroshun.configapi.core.serialization.key.KeyGenerator;
import dev.siroshun.configapi.core.serialization.registry.SerializerRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link Serializer} implementation for serializing {@link Record} object to {@link MapNode}.
 *
 * @param <R> the type of the {@link Record}
 */
@ApiStatus.Experimental
public final class RecordSerializer<R extends Record> implements Serializer<R, MapNode> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final RecordSerializer DEFAULT = new RecordSerializer(SerializerRegistry.empty(), KeyGenerator.AS_IS);

    /**
     * Gets the default {@link RecordSerializer}.
     * <p>
     * This serializer uses {@link KeyGenerator#AS_IS}.
     *
     * @param <R> the type of the {@link Record}
     * @return the default {@link RecordSerializer}
     */
    @SuppressWarnings("unchecked")
    public static <R extends Record> @NotNull RecordSerializer<R> serializer() {
        return DEFAULT;
    }

    /**
     * Creates a {@link RecordSerializer} with the {@link KeyGenerator}.
     * <p>
     * If specifying {@link KeyGenerator#AS_IS}, this method returns {@link #serializer()}.
     *
     * @param keyGenerator the {@link KeyGenerator} to generate key name
     * @param <R>          the type of the {@link Record}
     * @return the {@link RecordSerializer}
     */
    @SuppressWarnings("unchecked")
    public static <R extends Record> @NotNull RecordSerializer<R> create(@NotNull KeyGenerator keyGenerator) {
        return keyGenerator == KeyGenerator.AS_IS ? DEFAULT : new RecordSerializer<>(SerializerRegistry.empty(), Objects.requireNonNull(keyGenerator));
    }

    /**
     * Creates a new {@link Builder} of {@link RecordSerializer}.
     *
     * @param <R> the type of the {@link Record}
     * @return a new {@link Builder} of {@link RecordSerializer}
     */
    public static <R extends Record> @NotNull Builder<R> builder() {
        return new Builder<>();
    }

    private final SerializerRegistry<Node<?>> serializerRegistry;
    private final KeyGenerator keyGenerator;

    RecordSerializer(@NotNull SerializerRegistry<Node<?>> serializerRegistry, @NotNull KeyGenerator keyGenerator) {
        this.serializerRegistry = serializerRegistry;
        this.keyGenerator = keyGenerator;
    }

    /**
     * {@inheritDoc}
     *
     * @throws SerializationException if {@link Serializer} for the custom objects is not found, etc
     */
    @Override
    public @NotNull MapNode serialize(@NotNull R input) throws SerializationException {
        return this.serialize0(Objects.requireNonNull(input));
    }

    /**
     * Creates a default {@link MapNode} from the given record class.
     *
     * @param clazz the {@link Record} to create {@link MapNode}
     * @return a default {@link MapNode} of the given record class
     * @throws SerializationException if {@link Serializer} for the custom objects is not found, etc
     */
    public @NotNull MapNode serializeDefault(@NotNull Class<? extends R> clazz) throws SerializationException {
        return this.serialize0(RecordUtils.createDefaultRecord(Objects.requireNonNull(clazz)));
    }

    private @NotNull MapNode serialize0(@NotNull Record input) throws SerializationException {
        var mapNode = MapNode.create();
        this.serializeRecord(input, mapNode);
        return mapNode;
    }

    private void serializeRecord(@NotNull Record record, @NotNull MapNode target) {
        var components = record.getClass().getRecordComponents();

        for (var component : components) {
            var value = RecordUtils.getValue(component, record);

            if (value == null) {
                continue;
            }

            var commentAnnotation = component.getDeclaredAnnotation(Comment.class);
            var comment = commentAnnotation != null ? SimpleComment.create(commentAnnotation.value(), commentAnnotation.type()) : null;

            if (component.getType().isRecord()) {
                if (component.isAnnotationPresent(Inline.class)) {
                    this.serializeRecord((Record) value, target);
                } else {
                    var mapNode = MapNode.create();
                    mapNode.setComment(comment);
                    this.serializeRecord((Record) value, mapNode);
                    target.set(RecordUtils.getKey(component, this.keyGenerator), mapNode);
                }
            } else {
                var serialized = this.serializeValue(value);

                if (serialized != null && serialized != NullNode.NULL) {
                    target.set(RecordUtils.getKey(component, this.keyGenerator), comment != null ? CommentableNode.withComment(serialized, comment) : serialized);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Node<?> serializeValue(@NotNull Object obj) {
        var clazz = obj.getClass();

        if (clazz == Boolean.class) {
            return BooleanValue.fromBoolean((Boolean) obj);
        } else if (clazz == Character.class) {
            return new CharValue((Character) obj);
        } else if (clazz == String.class) {
            return StringValue.fromString((String) obj);
        } else if (Number.class.isAssignableFrom(clazz)) {
            return NumberValue.fromNumber((Number) obj);
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return new EnumValue((Enum) obj);
        }

        Serializer<Object, ? extends Node<?>> serializer = null;

        while (serializer == null && clazz != null) {
            serializer = (Serializer<Object, ? extends Node<?>>) this.serializerRegistry.get(clazz);
            clazz = clazz.getSuperclass();
        }

        if (serializer != null) {
            return serializer.serialize(obj);
        } else if (Record.class.isAssignableFrom(obj.getClass())) {
            return serialize0((Record) obj);
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            return serializeCollection((Collection<?>) obj);
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            return serializeMap((Map<?, ?>) obj);
        } else if (obj.getClass().isArray()) {
            return this.serializeArray(obj);
        } else {
            throw new SerializationException("No serializer found for " + obj.getClass());
        }
    }

    private @NotNull ListNode serializeCollection(@NotNull Collection<?> list) {
        var listNode = ListNode.create();

        for (var element : list) {
            if (element == null) {
                continue;
            }

            var serialized = serializeValue(element);

            if (serialized != null) {
                listNode.add(serialized);
            }
        }

        return listNode;
    }

    private @NotNull MapNode serializeMap(@NotNull Map<?, ?> map) {
        var mapNode = MapNode.create();

        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            if (key == null || value == null) {
                continue;
            }

            var serialized = serializeValue(value);

            if (serialized != null) {
                mapNode.set(key.toString(), serialized);
            }
        }

        return mapNode;
    }

    private @NotNull Node<?> serializeArray(@NotNull Object object) {
        var clazz = object.getClass();
        if (clazz == int[].class) {
            return new IntArray((int[]) object);
        } else if (clazz == long[].class) {
            return new LongArray((long[]) object);
        } else if (clazz == float[].class) {
            return new FloatArray((float[]) object);
        } else if (clazz == double[].class) {
            return new DoubleArray((double[]) object);
        } else if (clazz == byte[].class) {
            return new ByteArray((byte[]) object);
        } else if (clazz == short[].class) {
            return new ShortArray((short[]) object);
        } else if (clazz == boolean[].class) {
            return new BooleanArray((boolean[]) object);
        } else if (clazz == char[].class) {
            return new CharArray((char[]) object);
        } else if (!clazz.getComponentType().isPrimitive()) {
            var array = (Object[]) object;

            if (array.length == 0) {
                return ListNode.empty();
            } else {
                var listNode = ListNode.create(array.length);

                for (var element : array) {
                    listNode.add(this.serializeValue(element));
                }

                return listNode;
            }
        } else {
            throw new SerializationException("Unsupported array type: " + object.getClass());
        }
    }

    /**
     * A {@link Builder} class for {@link RecordSerializer}.
     *
     * @param <R> the type of the {@link Record}
     */
    public static final class Builder<R extends Record> {

        private SerializerRegistry<Node<?>> serializerRegistry;
        private KeyGenerator keyGenerator = KeyGenerator.AS_IS;

        private Builder() {
        }

        /**
         * Adds a {@link Serializer} for the specifies {@link Class}.
         *
         * @param clazz      a type of objects to serialize
         * @param serializer a {@link Serializer}
         * @param <T>        a type of objects to serialize
         * @return this {@link Builder} instance
         */
        @Contract("_, _ -> this")
        public <T> @NotNull Builder<R> addSerializer(@NotNull Class<T> clazz, @NotNull Serializer<? super T, ? extends Node<?>> serializer) {
            this.getSerializerRegistry().register(clazz, serializer);
            return this;
        }

        /**
         * Adds {@link Serializer}s in the {@link SerializerRegistry}.
         *
         * @param registry a {@link SerializerRegistry} that contains {@link Serializer}s to register
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder<R> addSerializers(@NotNull SerializerRegistry<Node<?>> registry) {
            this.getSerializerRegistry().registerAll(registry);
            return this;
        }

        /**
         * Sets the {@link KeyGenerator}.
         *
         * @param keyGenerator the {@link KeyGenerator} to generate keys
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder<R> keyGenerator(@NotNull KeyGenerator keyGenerator) {
            this.keyGenerator = Objects.requireNonNull(keyGenerator);
            return this;
        }

        /**
         * Creates a {@link RecordSerializer} from this {@link Builder}.
         *
         * @return a {@link RecordSerializer}
         * @see #create(KeyGenerator)
         */
        public @NotNull RecordSerializer<R> build() {
            return new RecordSerializer<>(this.getFrozenSerializerRegistry(), this.keyGenerator);
        }

        private @NotNull SerializerRegistry<Node<?>> getSerializerRegistry() {
            if (this.serializerRegistry == null) {
                this.serializerRegistry = SerializerRegistry.create();
            }
            return this.serializerRegistry;
        }

        private @NotNull SerializerRegistry<Node<?>> getFrozenSerializerRegistry() {
            if (this.serializerRegistry == null) {
                return SerializerRegistry.empty();
            } else {
                this.serializerRegistry.freeze();
                return this.serializerRegistry;
            }
        }
    }
}
