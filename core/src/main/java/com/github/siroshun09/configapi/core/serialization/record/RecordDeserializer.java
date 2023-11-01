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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.Deserializer;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import com.github.siroshun09.configapi.core.serialization.annotation.MapType;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.core.serialization.registry.DeserializerRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link Deserializer} implementation for deserializing {@link MapNode} to {@link Record} object.
 *
 * @param <R> the type of the {@link Record}
 */
@ApiStatus.Experimental
public final class RecordDeserializer<R extends Record> implements Deserializer<MapNode, R> {

    /**
     * Creates {@link RecordDeserializer} of the specified {@link Record} class.
     *
     * @param recordClass the class of the {@link Record}
     * @param <R>         the type of the {@link Record}
     * @return {@link RecordDeserializer} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull Class<R> recordClass) {
        return create(recordClass, KeyGenerator.AS_IS);
    }

    /**
     * Creates {@link RecordDeserializer} of the specified {@link Record} class.
     *
     * @param recordClass  the class of the {@link Record}
     * @param keyGenerator the {@link KeyGenerator} to generate keys from field names
     * @param <R>          the type of the {@link Record}
     * @return {@link RecordDeserializer} of the specified {@link Record} class
     */
    @Contract("_, _ -> new")
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull Class<R> recordClass, @NotNull KeyGenerator keyGenerator) {
        return new RecordDeserializer<>(recordClass, DeserializerRegistry.empty(), keyGenerator, null);
    }

    /**
     * Creates {@link RecordDeserializer} with the default record.
     *
     * @param defaultRecord the default {@link Record} to get the default value if the value is not found in the {@link MapNode}
     * @param <R>           the type of the {@link Record}
     * @return {@link RecordDeserializer} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull R defaultRecord) {
        return create(defaultRecord, KeyGenerator.AS_IS);
    }

    /**
     * Creates {@link RecordDeserializer} with the default record.
     *
     * @param defaultRecord the default {@link Record} to get the default value if the value is not found in the {@link MapNode}
     * @param keyGenerator  the {@link KeyGenerator} to generate keys from field names
     * @param <R>           the type of the {@link Record}
     * @return {@link RecordDeserializer} of the specified {@link Record} class
     */
    @Contract("_, _ -> new")
    @SuppressWarnings("unchecked")
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull R defaultRecord, @NotNull KeyGenerator keyGenerator) {
        return new RecordDeserializer<>((Class<R>) defaultRecord.getClass(), DeserializerRegistry.empty(), keyGenerator, defaultRecord);
    }

    /**
     * Creates a new {@link Builder} of the specified {@link Record} class.
     *
     * @param recordClass the class of the {@link Record}
     * @param <R>         the type of the {@link Record}
     * @return {@link Builder} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull Builder<R> builder(@NotNull Class<R> recordClass) {
        return new Builder<>(recordClass);
    }

    /**
     * Creates a new {@link Builder} with the default record.
     *
     * @param defaultRecord the default {@link Record} to get the default value if the value is not found in the {@link MapNode}
     * @param <R>           the type of the {@link Record}
     * @return {@link Builder} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull Builder<R> builder(@NotNull R defaultRecord) {
        return new Builder<>(defaultRecord);
    }

    private final Class<R> recordClass;
    private final DeserializerRegistry<Node<?>> deserializerRegistry;
    private final KeyGenerator keyGenerator;
    private final @Nullable R defaultRecord;

    RecordDeserializer(@NotNull Class<R> recordClass,
                       @NotNull DeserializerRegistry<Node<?>> deserializerRegistry,
                       @NotNull KeyGenerator keyGenerator,
                       @Nullable R defaultRecord) {
        this.recordClass = recordClass;
        this.deserializerRegistry = deserializerRegistry;
        this.keyGenerator = keyGenerator;
        this.defaultRecord = defaultRecord;
    }

    /**
     * {@inheritDoc}
     *
     * @throws SerializationException if {@link Deserializer} for the custom objects is not found, etc
     */
    @Override
    public @NotNull R deserialize(@NotNull MapNode input) throws SerializationException {
        var components = this.recordClass.getRecordComponents();

        var types = new Class<?>[components.length];
        var args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            var component = components[i];
            var node = input.get(RecordUtils.getKey(component, this.keyGenerator));
            var type = component.getType();

            Object arg;

            if (Collection.class.isAssignableFrom(type)) {
                arg = this.deserializeToCollection(node, component);
            } else if (type == Map.class) {
                arg = this.deserializeToMap(node, component);
            } else if (type.isArray()) {
                arg = this.deserializeArray(node, component);
            } else if (node != NullNode.NULL) {
                var deserialized = this.deserializeNode(node, type);
                arg = deserialized != null ? deserialized : RecordUtils.getDefaultValue(component, this.defaultRecord);
            } else {
                arg = RecordUtils.getDefaultValue(component, this.defaultRecord);
            }

            types[i] = type;
            args[i] = arg;
        }

        return RecordUtils.createRecord(this.recordClass, types, args);
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object deserializeNode(@NotNull Node<?> node, @NotNull Class<?> clazz) {
        if (clazz == boolean.class || clazz == Boolean.class) {
            return node instanceof BooleanValue booleanValue && booleanValue.asBoolean();
        } else if (clazz == String.class) {
            return node instanceof StringValue stringValue ? stringValue.asString() : String.valueOf(node.value());
        } else if (Enum.class.isAssignableFrom(clazz)) {
            if (node instanceof EnumValue<?> enumValue && clazz.isInstance(enumValue.value())) {
                return enumValue.value();
            } else if (node instanceof StringValue stringValue) {
                var subClass = clazz.asSubclass(Enum.class);
                try {
                    return Enum.valueOf(subClass, stringValue.value());
                } catch (IllegalArgumentException ignored1) {
                    try {
                        return Enum.valueOf(subClass, stringValue.value().toUpperCase(Locale.ENGLISH));
                    } catch (IllegalArgumentException ignored2) {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else if (clazz == byte.class || clazz == Byte.class) {
            return node instanceof NumberValue value ? value.asByte() : 0;
        } else if (clazz == double.class || clazz == Double.class) {
            return node instanceof NumberValue value ? value.asDouble() : 0;
        } else if (clazz == float.class || clazz == Float.class) {
            return node instanceof NumberValue value ? value.asFloat() : 0;
        } else if (clazz == int.class || clazz == Integer.class) {
            return node instanceof NumberValue value ? value.asInt() : 0;
        } else if (clazz == long.class || clazz == Long.class) {
            return node instanceof NumberValue value ? value.asLong() : 0;
        } else if (clazz == short.class || clazz == Short.class) {
            return node instanceof NumberValue value ? value.asShort() : 0;
        }

        var deserializer = this.deserializerRegistry.get(clazz);

        if (deserializer != null) {
            return deserializer.deserialize(node);
        } else if (clazz.isRecord()) {
            return node instanceof MapNode mapNode ?
                    create(clazz.asSubclass(Record.class), this.keyGenerator).deserialize(mapNode) :
                    RecordUtils.createDefaultRecord(clazz);
        } else {
            throw new SerializationException("No deserializer found for " + clazz.getSimpleName());
        }
    }

    private @Nullable Collection<?> deserializeToCollection(@NotNull Node<?> node, @NotNull RecordComponent component) {
        if (!(node instanceof ListNode listNode)) {
            return this.defaultRecord != null ?
                    (Collection<?>) RecordUtils.getValue(component, this.defaultRecord) :
                    CollectionUtils.emptyCollectionOrNull(component.getType());
        }

        if (listNode.value().isEmpty()) {
            return CollectionUtils.emptyCollectionOrNull(component.getType());
        }

        var originalList = listNode.value();
        var collection = CollectionUtils.createCollection(component.getType(), originalList.size());

        if (collection == null) {
            return null;
        }

        var annotation = component.getDeclaredAnnotation(CollectionType.class);

        if (annotation == null) {
            throw new SerializationException("@CollectionType is not declared for " + component.getName());
        }

        var elementType = annotation.value();

        for (var elementNode : originalList) {
            var element = elementNode.value();

            if (element == null) {
                continue;
            }

            var deserialized = this.deserializeNode(elementNode, elementType);

            if (deserialized != null) {
                collection.add(deserialized);
            }
        }

        return CollectionUtils.unmodifiable(component.getType(), collection);
    }

    private @NotNull Map<?, ?> deserializeToMap(@NotNull Node<?> node, @NotNull RecordComponent component) {
        if (!(node instanceof MapNode mapNode)) {
            var def = this.defaultRecord != null ? (Map<?, ?>) RecordUtils.getValue(component, this.defaultRecord) : null;
            return def != null ? def : Collections.emptyMap();
        }

        if (mapNode.value().isEmpty()) {
            return Collections.emptyMap();
        }

        var map = new HashMap<>(mapNode.value().size(), 1.0f);

        var annotation = component.getDeclaredAnnotation(MapType.class);

        if (annotation == null) {
            throw new SerializationException("@MapType is not declared for " + component.getName());
        }

        var keyType = annotation.key();
        var valueType = annotation.value();

        for (var entry : mapNode.value().entrySet()) {
            var key = deserializeKey(entry.getKey(), keyType);
            var value = deserializeNode(entry.getValue(), valueType);

            if (key != null && value != null) {
                map.put(key, value);
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private @NotNull Object deserializeArray(@NotNull Node<?> node, @NotNull RecordComponent component) {
        var componentType = component.getType().getComponentType();

        if (componentType.isPrimitive()) {
            return deserializePrimitiveArray(node, component);
        }

        if (!(node instanceof ListNode listNode)) {
            return this.getDefaultOrEmptyArray(component, componentType);
        }

        if (listNode.value().isEmpty()) {
            return Array.newInstance(componentType, 0);
        }

        return listNode.asList(componentType).toArray(length -> createArray(componentType, length));
    }

    private @NotNull Object deserializePrimitiveArray(@NotNull Node<?> node, @NotNull RecordComponent component) {
        if (component.getType() == int[].class) {
            return node instanceof IntArray intArray ? intArray.value() : this.getDefaultOrEmptyArray(component, int.class);
        } else if (component.getType() == long[].class) {
            return node instanceof LongArray longArray ? longArray.value() : this.getDefaultOrEmptyArray(component, long.class);
        } else if (component.getType() == float[].class) {
            return node instanceof FloatArray floatArray ? floatArray.value() : this.getDefaultOrEmptyArray(component, float.class);
        } else if (component.getType() == double[].class) {
            return node instanceof DoubleArray doubleArray ? doubleArray.value() : this.getDefaultOrEmptyArray(component, double.class);
        } else if (component.getType() == byte[].class) {
            return node instanceof ByteArray byteArray ? byteArray.value() : this.getDefaultOrEmptyArray(component, byte.class);
        } else if (component.getType() == short[].class) {
            return node instanceof ShortArray shortArray ? shortArray.value() : this.getDefaultOrEmptyArray(component, short.class);
        } else if (component.getType() == boolean[].class) {
            return node instanceof BooleanArray booleanArray ? booleanArray.value() : this.getDefaultOrEmptyArray(component, boolean.class);
        } else {
            throw new IllegalArgumentException("Unexpected primitive type: " + component.getType());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] createArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    private @NotNull Object getDefaultOrEmptyArray(@NotNull RecordComponent component, @NotNull Class<?> componentType) {
        return this.defaultRecord != null ?
                RecordUtils.getValue(component, this.defaultRecord) :
                Array.newInstance(componentType, 0);
    }

    private @Nullable Object deserializeKey(@NotNull Object key, @NotNull Class<?> clazz) {
        if (clazz.isInstance(key)) {
            return key;
        }

        var deserializer = this.deserializerRegistry.get(clazz);

        if (deserializer != null) {
            return deserializer.apply(Node.fromObject(key));
        }

        return null;
    }

    /**
     * A {@link Builder} class for {@link RecordDeserializer}.
     *
     * @param <R> the type of the {@link Record}
     */
    public static final class Builder<R extends Record> {

        private final Class<R> recordClass;
        private final @Nullable R defaultRecord;

        private DeserializerRegistry<Node<?>> deserializerRegistry;
        private KeyGenerator keyGenerator = KeyGenerator.AS_IS;

        private Builder(@NotNull Class<R> recordClass) {
            this.recordClass = Objects.requireNonNull(recordClass);
            this.defaultRecord = null;
        }

        @SuppressWarnings("unchecked")
        private Builder(@NotNull R defaultRecord) {
            this.recordClass = (Class<R>) defaultRecord.getClass();
            this.defaultRecord = defaultRecord;
        }

        /**
         * Adds a {@link Deserializer} for the specifies {@link Class}.
         *
         * @param clazz        a type of objects after deserialization
         * @param deserializer a {@link Deserializer}
         * @param <T>          a type of objects after deserialization
         * @return this {@link Builder} instance
         */
        @Contract("_, _ -> this")
        public <T> @NotNull Builder<R> addDeserializer(@NotNull Class<T> clazz,
                                                       @NotNull Deserializer<? super Node<?>, ? extends T> deserializer) {
            this.getDeserializerRegistry().register(clazz, deserializer);
            return this;
        }

        /**
         * Adds {@link Deserializer}s in the {@link DeserializerRegistry}.
         *
         * @param registry a {@link DeserializerRegistry} that contains {@link Deserializer}s to register
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder<R> addDeserializers(@NotNull DeserializerRegistry<Node<?>> registry) {
            this.getDeserializerRegistry().registerAll(registry);
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
         * Creates a {@link RecordDeserializer} from this {@link Builder}.
         *
         * @return a {@link RecordDeserializer}
         */
        @Contract(value = "-> new", pure = true)
        public @NotNull RecordDeserializer<R> build() {
            return new RecordDeserializer<>(
                    Objects.requireNonNull(this.getRecordClass(), "recordClass is not set"),
                    this.getFrozenDeserializerRegistry(),
                    this.keyGenerator,
                    this.defaultRecord
            );
        }

        @SuppressWarnings("unchecked")
        private @Nullable Class<R> getRecordClass() {
            if (this.recordClass != null) {
                return this.recordClass;
            } else if (this.defaultRecord != null) {
                return (Class<R>) this.defaultRecord.getClass();
            } else {
                return null;
            }
        }

        private @NotNull DeserializerRegistry<Node<?>> getDeserializerRegistry() {
            if (this.deserializerRegistry == null) {
                this.deserializerRegistry = DeserializerRegistry.create();
            }
            return this.deserializerRegistry;
        }

        private @NotNull DeserializerRegistry<Node<?>> getFrozenDeserializerRegistry() {
            if (this.deserializerRegistry == null) {
                return DeserializerRegistry.empty();
            } else {
                this.deserializerRegistry.freeze();
                return this.deserializerRegistry;
            }
        }
    }
}
