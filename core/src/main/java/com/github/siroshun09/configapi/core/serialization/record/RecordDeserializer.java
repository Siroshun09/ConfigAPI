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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.CommentedNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.Deserializer;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.CollectionType;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultMapKey;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultNull;
import com.github.siroshun09.configapi.core.serialization.annotation.Inline;
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
import java.util.function.Supplier;

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
        return deserializeToRecord(this.recordClass, input, this.defaultRecord);
    }

    private <T extends Record> T deserializeToRecord(@NotNull Class<T> clazz, @NotNull MapNode input,
                                                     @Nullable T defaultRecord) {
        var components = clazz.getRecordComponents();

        var types = new Class<?>[components.length];
        var args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            var component = components[i];
            var node = input.get(RecordUtils.getKey(component, this.keyGenerator));
            types[i] = component.getType();
            args[i] = processComponent(component, node, RecordUtils.getDefaultValue(component, defaultRecord));
        }

        return RecordUtils.createRecord(clazz, types, args);
    }

    private Object processComponent(@NotNull RecordComponent component, @NotNull Node<?> node,
                                    @Nullable Object defaultValue) {
        var type = component.getType();
        var deserializer = this.deserializerRegistry.get(type);

        if (deserializer != null) {
            return deserializer.deserialize(node);
        } else if (CollectionUtils.isSupportedCollectionType(type)) {
            return this.processCollection(component, node, defaultValue);
        } else if (type == Map.class) {
            return this.processMap(component, node, defaultValue);
        } else if (type.isArray()) {
            return this.processArray(component, node, defaultValue);
        } else if (type.isRecord()) {
            return this.processRecord(component, node, defaultValue);
        } else {
            return this.deserializeNode(node, type, defaultValue);
        }
    }

    private Object processCollection(@NotNull RecordComponent component, @NotNull Node<?> node, @Nullable Object defaultCollection) {
        var annotation = component.getDeclaredAnnotation(CollectionType.class);

        if (annotation == null) {
            throw new SerializationException("@CollectionType is not declared for " + component.getName());
        }

        if (node instanceof ListNode listNode) {
            return this.deserializeToCollection(listNode, component.getType(), annotation.value());
        } else if (defaultCollection != null) {
            return defaultCollection;
        } else {
            return component.isAnnotationPresent(DefaultNull.class) ? null : CollectionUtils.emptyCollectionOrNull(component.getType());
        }
    }

    private Object processMap(@NotNull RecordComponent component, @NotNull Node<?> node, @Nullable Object defaultMap) {
        var annotation = component.getDeclaredAnnotation(MapType.class);

        if (annotation == null) {
            throw new SerializationException("@MapType is not declared for " + component.getName());
        }

        var keyType = annotation.key();
        var valueType = annotation.value();
        var defaultMapKey = component.getDeclaredAnnotation(DefaultMapKey.class);

        if (node instanceof MapNode mapNode) {
            return this.deserializeToMap(mapNode, keyType, valueType, defaultMapKey);
        } else if (defaultMap != null) {
            return defaultMap;
        } else if (keyType == String.class && defaultMapKey != null) {
            var defaultValue = this.deserializeNode(MapNode.empty(), valueType, RecordUtils.createDefaultValue(valueType, false));
            return defaultValue != null ? Map.of(defaultMapKey.value(), defaultValue) : null;
        } else {
            return component.isAnnotationPresent(DefaultNull.class) ? null : Collections.emptyMap();
        }
    }

    private @NotNull Object processArray(@NotNull RecordComponent component, @NotNull Node<?> node, @Nullable Object defaultArray) {
        return this.deserializeToArray(
                node,
                component.getType(),
                () -> {
                    if (defaultArray != null) return defaultArray;
                    else if (component.isAnnotationPresent(DefaultNull.class)) return null;
                    else return createArray(component.getType().getComponentType(), 0);
                }
        );
    }

    private Object processRecord(@NotNull RecordComponent component, @NotNull Node<?> node, @Nullable Object defaultValue) {
        var clazz = component.getType();

        Object result;

        if (component.isAnnotationPresent(Inline.class)) {
            result = this.processInlinedRecord(component, clazz, node, (Record) defaultValue);
        } else {
            result = this.deserializeNode(node, clazz, defaultValue);
        }

        return result;
    }

    private Object processInlinedRecord(@NotNull RecordComponent parent, @NotNull Class<?> clazz, @NotNull Node<?> node, @Nullable Record defaultRecord) {
        var components = clazz.getRecordComponents();

        if (components.length != 1) {
            throw new SerializationException("The component of the record for which @Inline is specified must be one.");
        }

        var inlinedComponent = components[0];
        var type = inlinedComponent.getType();

        Object defaultObject;

        if (defaultRecord != null) {
            defaultObject = RecordUtils.getValue(inlinedComponent, defaultRecord);
        } else {
            var def = RecordUtils.getDefaultValueByAnnotation(type, parent);
            if (def == null)
                def = RecordUtils.getDefaultValueByAnnotation(type, inlinedComponent);
            if (def == null)
                def = RecordUtils.createDefaultValue(type, inlinedComponent.isAnnotationPresent(DefaultNull.class));
            defaultObject = def;
        }

        return RecordUtils.createRecord(clazz, new Class[]{type}, new Object[]{this.processComponent(inlinedComponent, node, defaultObject)});
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object deserializeNode(@NotNull Node<?> node, @NotNull Class<?> clazz,
                                             @Nullable Object defaultObject) {
        if (node instanceof CommentedNode<?> commentedNode) {
            return this.deserializeNode(commentedNode.node(), clazz, defaultObject);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return node instanceof BooleanValue booleanValue ? booleanValue.value() : defaultObject;
        } else if (clazz == String.class) {
            return node instanceof StringValue stringValue ? stringValue.asString() : defaultObject;
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
            return node instanceof NumberValue value ? value.asByte() : defaultObject;
        } else if (clazz == double.class || clazz == Double.class) {
            return node instanceof NumberValue value ? value.asDouble() : defaultObject;
        } else if (clazz == float.class || clazz == Float.class) {
            return node instanceof NumberValue value ? value.asFloat() : defaultObject;
        } else if (clazz == int.class || clazz == Integer.class) {
            return node instanceof NumberValue value ? value.asInt() : defaultObject;
        } else if (clazz == long.class || clazz == Long.class) {
            return node instanceof NumberValue value ? value.asLong() : defaultObject;
        } else if (clazz == short.class || clazz == Short.class) {
            return node instanceof NumberValue value ? value.asShort() : defaultObject;
        }

        var deserializer = this.deserializerRegistry.get(clazz);

        if (deserializer != null) {
            return deserializer.deserialize(node);
        } else if (CollectionUtils.isSupportedCollectionType(clazz)) {
            return node instanceof ListNode listNode ? this.deserializeToCollection(listNode, clazz, Object.class) : defaultObject;
        } else if (clazz == Map.class) {
            return node instanceof MapNode mapNode ? this.deserializeToMap(mapNode, Object.class, Object.class, null) : defaultObject;
        } else if (clazz.isArray()) {
            return this.deserializeToArray(node, clazz, () -> defaultObject);
        } else if (clazz.isRecord()) {
            var mapNode = node instanceof MapNode casted ? casted : MapNode.empty();
            return this.deserializeToRecord(clazz.asSubclass(Record.class), mapNode, (Record) defaultObject);
        } else {
            throw new SerializationException("No deserializer found for " + clazz.getSimpleName());
        }
    }

    private @NotNull Collection<?> deserializeToCollection(@NotNull ListNode node,
                                                           @NotNull Class<?> collectionType,
                                                           @NotNull Class<?> elementType) {
        if (node.value().isEmpty()) {
            return CollectionUtils.emptyCollectionOrNull(collectionType);
        }

        var originalList = node.value();
        var collection = CollectionUtils.createCollection(collectionType, originalList.size());

        for (var elementNode : originalList) {
            var element = elementNode.value();

            if (element == null) {
                continue;
            }

            var deserialized = this.deserializeNode(elementNode, elementType, null);

            if (deserialized != null) {
                collection.add(deserialized);
            }
        }

        return CollectionUtils.unmodifiable(collectionType, collection);
    }

    private @NotNull Map<?, ?> deserializeToMap(@NotNull MapNode node,
                                                @NotNull Class<?> keyType, @NotNull Class<?> valueType,
                                                @Nullable DefaultMapKey defaultMapKey) {
        if (node.value().isEmpty()) {
            return Collections.emptyMap();
        }

        var map = new HashMap<>(node.value().size(), 1.0f);

        for (var entry : node.value().entrySet()) {
            var key = deserializeKey(entry.getKey(), keyType);
            var value = deserializeNode(entry.getValue(), valueType, null);

            if (key != null && value != null) {
                map.put(key, value);
            }
        }

        if (keyType.equals(String.class) && defaultMapKey != null && !map.containsKey(defaultMapKey.value())) {
            var defaultValue = this.deserializeNode(MapNode.empty(), valueType, null);
            if (defaultValue != null) {
                map.put(defaultMapKey.value(), defaultValue);
            }
        }

        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    private @NotNull Object deserializeToArray(@NotNull Node<?> node, @NotNull Class<?> clazz, @NotNull Supplier<Object> defaultArraySupplier) {
        if (clazz == int[].class) {
            return node instanceof IntArray intArray ? intArray.value() : defaultArraySupplier.get();
        } else if (clazz == long[].class) {
            return node instanceof LongArray longArray ? longArray.value() : defaultArraySupplier.get();
        } else if (clazz == float[].class) {
            return node instanceof FloatArray floatArray ? floatArray.value() : defaultArraySupplier.get();
        } else if (clazz == double[].class) {
            return node instanceof DoubleArray doubleArray ? doubleArray.value() : defaultArraySupplier.get();
        } else if (clazz == byte[].class) {
            return node instanceof ByteArray byteArray ? byteArray.value() : defaultArraySupplier.get();
        } else if (clazz == short[].class) {
            return node instanceof ShortArray shortArray ? shortArray.value() : defaultArraySupplier.get();
        } else if (clazz == boolean[].class) {
            return node instanceof BooleanArray booleanArray ? booleanArray.value() : defaultArraySupplier.get();
        } else if (node instanceof ListNode listNode) {
            var list = listNode.value();
            var componentType = clazz.getComponentType();
            if (list.isEmpty()) {
                return createArray(componentType, 0);
            } else {
                var array = createArray(componentType, list.size());
                for (int i = 0, size = list.size(); i < size; i++) {
                    var element = list.get(i);
                    if (componentType.isInstance(element)) {
                        array[i] = element;
                    } else if (componentType.isInstance(element.value())) {
                        array[i] = element.value();
                    } else {
                        array[i] = deserializeNode(list.get(i), componentType, null);
                    }
                }
                return array;
            }
        } else {
            return defaultArraySupplier.get();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] createArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
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
