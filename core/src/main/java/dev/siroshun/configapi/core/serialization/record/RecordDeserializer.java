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

import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.DoubleArray;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.FloatArray;
import dev.siroshun.configapi.core.node.IntArray;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.LongArray;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NumberValue;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.core.serialization.Deserializer;
import dev.siroshun.configapi.core.serialization.SerializationException;
import dev.siroshun.configapi.core.serialization.annotation.CollectionType;
import dev.siroshun.configapi.core.serialization.annotation.DefaultMapKey;
import dev.siroshun.configapi.core.serialization.annotation.Inline;
import dev.siroshun.configapi.core.serialization.annotation.MapType;
import dev.siroshun.configapi.core.serialization.key.KeyGenerator;
import dev.siroshun.configapi.core.serialization.registry.DeserializerRegistry;
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
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull Class<? extends R> recordClass) {
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
    public static <R extends Record> @NotNull RecordDeserializer<R> create(@NotNull Class<? extends R> recordClass, @NotNull KeyGenerator keyGenerator) {
        return new RecordDeserializer<>(Objects.requireNonNull(recordClass), DeserializerRegistry.empty(), Objects.requireNonNull(keyGenerator), null);
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
        return new RecordDeserializer<>((Class<? extends R>) defaultRecord.getClass(), DeserializerRegistry.empty(), Objects.requireNonNull(keyGenerator), defaultRecord);
    }

    /**
     * Creates a new {@link Builder} of the specified {@link Record} class.
     *
     * @param recordClass the class of the {@link Record}
     * @param <R>         the type of the {@link Record}
     * @return {@link Builder} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull Builder<R> builder(@NotNull Class<? extends R> recordClass) {
        return new Builder<>(Objects.requireNonNull(recordClass));
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
        return new Builder<>(Objects.requireNonNull(defaultRecord));
    }

    private final Class<? extends R> recordClass;
    private final DeserializerRegistry<Node<?>> deserializerRegistry;
    private final KeyGenerator keyGenerator;
    private final @Nullable R defaultRecord;

    RecordDeserializer(@NotNull Class<? extends R> recordClass,
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
        return this.deserializeToRecord(this.recordClass, Objects.requireNonNull(input), () -> this.defaultRecord);
    }

    private <T extends Record> @NotNull T deserializeToRecord(@NotNull Class<T> clazz, @NotNull MapNode input,
                                                              @NotNull DefaultRecordSupplier<?> defaultRecordSupplier) {
        var components = clazz.getRecordComponents();

        var types = new Class<?>[components.length];
        var args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            var component = components[i];

            var type = component.getType();
            types[i] = type;

            var deserializer = this.deserializerRegistry.get(type);
            var key = RecordUtils.getKey(component, this.keyGenerator);

            if (deserializer != null) {
                args[i] = deserializer.deserialize(input.get(key));
            } else if (type.isRecord()) {
                MapNode source = component.isAnnotationPresent(Inline.class) ? input : input.getMap(key);
                args[i] = this.deserializeToRecord(type.asSubclass(Record.class), source, defaultRecordSupplier.defaultRecord(component));
            } else if (CollectionUtils.isSupportedCollectionType(type)) {
                args[i] = this.processCollection(component, input.get(key), defaultRecordSupplier.defaultValue(component));
            } else if (type == Map.class) {
                args[i] = this.processMap(component, input.get(key), defaultRecordSupplier.defaultValue(component));
            } else if (type.isArray()) {
                args[i] = this.deserializeToArray(input.get(key), component.getType(), defaultRecordSupplier.defaultValue(component));
            } else {
                args[i] = this.deserializeNode(input.get(key), type, defaultRecordSupplier.defaultValue(component));
            }
        }

        return RecordUtils.createRecord(clazz, types, args);
    }

    private @Nullable Object processCollection(@NotNull RecordComponent component, @NotNull Node<?> node, @NotNull DefaultValueSupplier<?> defaultCollectionSupplier) {
        var annotation = component.getDeclaredAnnotation(CollectionType.class);

        if (annotation == null) {
            throw new SerializationException("@CollectionType is not declared for " + component.getName());
        }

        if (node instanceof ListNode listNode) {
            return this.deserializeToCollection(listNode, component.getType(), annotation.value());
        } else {
            return defaultCollectionSupplier.get();
        }
    }

    private @Nullable Object processMap(@NotNull RecordComponent component, @NotNull Node<?> node, @NotNull DefaultValueSupplier<?> defaultMapSupplier) {
        var annotation = component.getDeclaredAnnotation(MapType.class);

        if (annotation == null) {
            throw new SerializationException("@MapType is not declared for " + component.getName());
        }

        var keyType = annotation.key();
        var valueType = annotation.value();
        var defaultMapKey = component.getDeclaredAnnotation(DefaultMapKey.class);

        if (node instanceof MapNode mapNode) {
            return this.deserializeToMap(mapNode, keyType, valueType, defaultMapKey);
        } else {
            return defaultMapSupplier.get();
        }
    }

    private @Nullable Object processArray(@NotNull RecordComponent component, @NotNull Node<?> node, @NotNull DefaultValueSupplier<?> defaultArraySupplier) {
        return this.deserializeToArray(node, component.getType(), defaultArraySupplier);
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object deserializeNode(@NotNull Node<?> node, @NotNull Class<?> clazz, @NotNull DefaultValueSupplier<?> defaultValueSupplier) {
        if (node instanceof CommentedNode<?> commentedNode) {
            return this.deserializeNode(commentedNode.node(), clazz, defaultValueSupplier);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return node instanceof BooleanValue booleanValue ? booleanValue.value() : defaultValueSupplier.get();
        } else if (clazz == char.class || clazz == Character.class) {
            return node instanceof CharValue charValue ? charValue.value() : defaultValueSupplier.get();
        } else if (clazz == String.class) {
            return node instanceof StringValue stringValue ? stringValue.asString() : defaultValueSupplier.get();
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
                        return defaultValueSupplier.get();
                    }
                }
            } else {
                return defaultValueSupplier.get();
            }
        } else if (clazz == byte.class || clazz == Byte.class) {
            return node instanceof NumberValue value ? value.asByte() : defaultValueSupplier.get();
        } else if (clazz == double.class || clazz == Double.class) {
            return node instanceof NumberValue value ? value.asDouble() : defaultValueSupplier.get();
        } else if (clazz == float.class || clazz == Float.class) {
            return node instanceof NumberValue value ? value.asFloat() : defaultValueSupplier.get();
        } else if (clazz == int.class || clazz == Integer.class) {
            return node instanceof NumberValue value ? value.asInt() : defaultValueSupplier.get();
        } else if (clazz == long.class || clazz == Long.class) {
            return node instanceof NumberValue value ? value.asLong() : defaultValueSupplier.get();
        } else if (clazz == short.class || clazz == Short.class) {
            return node instanceof NumberValue value ? value.asShort() : defaultValueSupplier.get();
        }

        var deserializer = this.deserializerRegistry.get(clazz);

        if (deserializer != null) {
            return deserializer.deserialize(node);
        } else if (CollectionUtils.isSupportedCollectionType(clazz)) {
            return node instanceof ListNode listNode ? this.deserializeToCollection(listNode, clazz, Object.class) : defaultValueSupplier.get();
        } else if (clazz == Map.class) {
            return node instanceof MapNode mapNode ? this.deserializeToMap(mapNode, Object.class, Object.class, null) : defaultValueSupplier.get();
        } else if (clazz.isArray()) {
            return this.deserializeToArray(node, clazz, defaultValueSupplier);
        } else if (clazz.isRecord()) {
            var mapNode = node instanceof MapNode casted ? casted : MapNode.empty();
            return this.deserializeToRecord(clazz.asSubclass(Record.class), mapNode, defaultValueSupplier.asRecord());
        } else {
            throw new SerializationException("No deserializer found for " + clazz.getSimpleName());
        }
    }

    private @NotNull Collection<?> deserializeToCollection(@NotNull ListNode node,
                                                           @NotNull Class<?> collectionType,
                                                           @NotNull Class<?> elementType) {
        if (node.value().isEmpty()) {
            return CollectionUtils.emptyCollection(collectionType);
        }

        var originalList = node.value();
        var collection = CollectionUtils.createCollection(collectionType, originalList.size());

        for (var elementNode : originalList) {
            var element = elementNode.value();

            if (element == null) {
                continue;
            }

            var deserialized = this.deserializeNode(elementNode, elementType, DefaultValueSupplier.nullSupplier());

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
            var value = deserializeNode(entry.getValue(), valueType, DefaultValueSupplier.nullSupplier());

            if (key != null && value != null) {
                map.put(key, value);
            }
        }

        if (keyType.equals(String.class) && defaultMapKey != null && !map.containsKey(defaultMapKey.value())) {
            var defaultValue = this.deserializeNode(MapNode.empty(), valueType, DefaultValueSupplier.nullSupplier());
            if (defaultValue != null) {
                map.put(defaultMapKey.value(), defaultValue);
            }
        }

        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    private @Nullable Object deserializeToArray(@NotNull Node<?> node, @NotNull Class<?> clazz, @NotNull DefaultValueSupplier<?> defaultArraySupplier) {
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
        } else if (clazz == char[].class) {
            return node instanceof CharArray charArray ? charArray.value() : defaultArraySupplier.get();
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
                        array[i] = deserializeNode(list.get(i), componentType, DefaultValueSupplier.nullSupplier());
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

        private final Class<? extends R> recordClass;
        private final @Nullable R defaultRecord;

        private DeserializerRegistry<Node<?>> deserializerRegistry;
        private KeyGenerator keyGenerator = KeyGenerator.AS_IS;

        private Builder(@NotNull Class<? extends R> recordClass) {
            this.recordClass = recordClass;
            this.defaultRecord = null;
        }

        @SuppressWarnings("unchecked")
        private Builder(@NotNull R defaultRecord) {
            this.recordClass = (Class<? extends R>) defaultRecord.getClass();
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
                    this.recordClass,
                    this.getFrozenDeserializerRegistry(),
                    this.keyGenerator,
                    this.defaultRecord
            );
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

    private interface DefaultValueSupplier<T> {

        @SuppressWarnings("rawtypes")
        DefaultValueSupplier NULL = () -> null;

        @SuppressWarnings("unchecked")
        static <T> DefaultValueSupplier<T> nullSupplier() {
            return (DefaultValueSupplier<T>) NULL;
        }

        @Nullable T get();

        default <N> @NotNull DefaultValueSupplier<N> as(@NotNull Class<N> clazz) {
            return () -> clazz.cast(this.get());
        }

        @SuppressWarnings("unchecked")
        default <R extends Record> @NotNull DefaultRecordSupplier<R> asRecord() {
            return new CachingDefaultRecordSupplier<>(() -> (R) this.get());
        }
    }

    private interface DefaultRecordSupplier<R extends Record> extends DefaultValueSupplier<R> {

        @SuppressWarnings("unchecked")
        default <N> @NotNull DefaultValueSupplier<N> defaultValue(@NotNull RecordComponent component) {
            return () -> (N) RecordUtils.getDefaultValue(component, this.get());
        }

        @SuppressWarnings("unchecked")
        default <N extends Record> @NotNull DefaultRecordSupplier<N> defaultRecord(@NotNull RecordComponent component) {
            return new CachingDefaultRecordSupplier<>(() -> (N) RecordUtils.getDefaultValue(component, this.get()));
        }
    }

    private static class CachingDefaultRecordSupplier<R extends Record> implements DefaultRecordSupplier<R> {

        private final DefaultRecordSupplier<R> supplier;
        private boolean cached;
        private R cache;

        private CachingDefaultRecordSupplier(@NotNull DefaultRecordSupplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public @Nullable R get() {
            if (!this.cached) {
                this.cached = true;
                this.cache = this.supplier.get();
            }
            return this.cache;
        }
    }
}
