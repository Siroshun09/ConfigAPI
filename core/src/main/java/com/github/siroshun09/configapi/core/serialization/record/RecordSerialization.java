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

import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.serialization.Deserializer;
import com.github.siroshun09.configapi.core.serialization.Serialization;
import com.github.siroshun09.configapi.core.serialization.Serializer;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.core.serialization.registry.DeserializerRegistry;
import com.github.siroshun09.configapi.core.serialization.registry.SerializationRegistry;
import com.github.siroshun09.configapi.core.serialization.registry.SerializerRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A {@link Serialization} implementation for {@link Record} class.
 *
 * @param serializer   the {@link RecordSerializer}
 * @param deserializer the {@link RecordDeserializer}
 * @param <R>          the type of the {@link Record}
 */
@ApiStatus.Experimental
public record RecordSerialization<R extends Record>(@NotNull Serializer<R, MapNode> serializer,
                                                    @NotNull Deserializer<MapNode, R> deserializer) implements Serialization<R, MapNode> {

    /**
     * Creates a new {@link RecordSerialization} of the specified {@link Record} class.
     *
     * @param recordClass a class of the {@link Record}
     * @param <R>         the type of the {@link Record}
     * @return a new {@link RecordSerialization} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull RecordSerialization<R> create(@NotNull Class<R> recordClass) {
        return new RecordSerialization<>(
                RecordSerializer.serializer(),
                RecordDeserializer.create(recordClass)
        );
    }

    /**
     * Creates a new {@link RecordSerialization} with the default record.
     *
     * @param defaultRecord the default {@link Record} to get the default value if the value is not found in the {@link MapNode}
     * @param <R>           the type of the {@link Record}
     * @return a new {@link RecordSerialization} of the specified {@link Record} class
     */
    @Contract("_ -> new")
    public static <R extends Record> @NotNull RecordSerialization<R> create(@NotNull R defaultRecord) {
        return new RecordSerialization<>(
                RecordSerializer.serializer(),
                RecordDeserializer.create(defaultRecord)
        );
    }

    /**
     * Creates a new {@link Builder} of the specified {@link Record} class.
     *
     * @param recordClass a class of the {@link Record}
     * @param <R>         the type of the {@link Record}
     * @return a new {@link Builder} of {@link RecordSerialization} for the specified {@link Record} class
     */
    @Contract(value = "_ -> new", pure = true)
    public static <R extends Record> @NotNull Builder<R> builder(@NotNull Class<R> recordClass) {
        return new RecordSerialization.Builder<>(recordClass);
    }

    /**
     * Creates a new {@link Builder}  with the default record.
     *
     * @param defaultRecord the default {@link Record} to get the default value if the value is not found in the {@link MapNode}
     * @param <R>           the type of the {@link Record}
     * @return a new {@link Builder} of {@link RecordSerialization} for the specified {@link Record} class
     */
    @SuppressWarnings("unchecked")
    @Contract(value = "_ -> new", pure = true)
    public static <R extends Record> @NotNull Builder<R> builder(@NotNull R defaultRecord) {
        return builder((Class<R>) defaultRecord.getClass()).defaultRecord(defaultRecord);
    }

    @Override
    public boolean hasSerializer() {
        return true;
    }

    @Override
    public boolean hasDeserializer() {
        return true;
    }

    /**
     * A {@link Builder} class for {@link RecordSerialization}.
     *
     * @param <R> the type of the {@link Record}
     */
    public static final class Builder<R extends Record> {

        private final Class<R> recordClass;
        private SerializationRegistry<Node<?>> serializationRegistry;
        private KeyGenerator keyGenerator = KeyGenerator.AS_IS;
        private R defaultRecord;

        private Builder(Class<R> recordClass) {
            this.recordClass = recordClass;
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
        public <T> @NotNull Builder<R> addSerializer(@NotNull Class<T> clazz,
                                                     @NotNull Serializer<? super T, ? extends Node<?>> serializer) {
            this.getSerializationRegistry().asSerializerRegistry().register(clazz, serializer);
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
            this.getSerializationRegistry().asSerializerRegistry().registerAll(registry);
            return this;
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
            this.getSerializationRegistry().asDeserializerRegistry().register(clazz, deserializer);
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
            this.getSerializationRegistry().asDeserializerRegistry().registerAll(registry);
            return this;
        }

        /**
         * Adds a {@link Serialization} for the specifies {@link Class}.
         *
         * @param clazz        a type of objects of the {@link Serialization}
         * @param serialization a {@link Serialization}
         * @param <T>         a type of objects of the {@link Serialization}
         * @return this {@link Builder} instance
         */
        @Contract("_, _ -> this")
        public <T> @NotNull Builder<R> addSerialization(@NotNull Class<T> clazz,
                                                        @NotNull Serialization<? super T, ? extends Node<?>> serialization) {
            this.getSerializationRegistry().register(clazz, serialization);
            return this;
        }

        /**
         * Adds {@link Serialization}s in the {@link SerializationRegistry}.
         *
         * @param registry a {@link SerializationRegistry} that contains {@link Serialization}s to register
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder<R> addSerialization(@NotNull SerializationRegistry<Node<?>> registry) {
            this.getSerializationRegistry().registerAll(registry);
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
         * Sets the default {@link Record}.
         *
         * @param defaultRecord the default {@link Record}
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder<R> defaultRecord(@NotNull R defaultRecord) {
            this.defaultRecord = Objects.requireNonNull(defaultRecord);
            return this;
        }

        /**
         * Creates {@link RecordSerialization} from this {@link Builder}.
         *
         * @return a new {@link RecordSerialization}
         */
        @Contract("-> new")
        public @NotNull RecordSerialization<R> build() {
            var registry = this.getFrozenSerializationRegistry();
            return new RecordSerialization<>(
                    new RecordSerializer<>(registry.asSerializerRegistry(), this.keyGenerator),
                    new RecordDeserializer<>(this.recordClass, registry.asDeserializerRegistry(), this.keyGenerator, this.defaultRecord)
            );
        }

        private @NotNull SerializationRegistry<Node<?>> getSerializationRegistry() {
            if (this.serializationRegistry == null) {
                this.serializationRegistry = SerializationRegistry.create();
            }
            return this.serializationRegistry;
        }

        private @NotNull SerializationRegistry<Node<?>> getFrozenSerializationRegistry() {
            if (this.serializationRegistry == null) {
                return SerializationRegistry.empty();
            } else {
                this.serializationRegistry.freeze();
                return this.serializationRegistry;
            }
        }
    }
}
