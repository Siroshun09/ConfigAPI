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

import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NumberValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.Serializer;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.core.serialization.registry.SerializerRegistry;
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
        return keyGenerator == KeyGenerator.AS_IS ? DEFAULT : new RecordSerializer<>(SerializerRegistry.empty(), keyGenerator);
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
        var components = input.getClass().getRecordComponents();
        var mapNode = MapNode.create();

        for (var component : components) {
            var value = RecordUtils.getValue(component, input);

            if (value == null) {
                continue;
            }

            var serialized = serializeValue(value);

            if (serialized != null) {
                mapNode.set(RecordUtils.getKey(component, keyGenerator), serialized);
            }
        }

        return mapNode;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Node<?> serializeValue(@NotNull Object obj) {
        var clazz = obj.getClass();

        if (clazz == Boolean.class) {
            return BooleanValue.fromBoolean((Boolean) obj);
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
            return create(this.keyGenerator).serialize((Record) obj);
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            return serializeCollection((Collection<?>) obj);
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            return serializeMap((Map<?, ?>) obj);
        } else if (obj.getClass().isArray()) {
            return Node.fromObject(obj);
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
            if (this.serializerRegistry == null) {
                return RecordSerializer.create(this.keyGenerator);
            } else {
                this.serializerRegistry.freeze();
                return new RecordSerializer<>(this.serializerRegistry, this.keyGenerator);
            }
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
