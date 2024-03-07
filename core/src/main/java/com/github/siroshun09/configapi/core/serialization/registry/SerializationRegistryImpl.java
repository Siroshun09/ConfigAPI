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

package com.github.siroshun09.configapi.core.serialization.registry;

import com.github.siroshun09.configapi.core.serialization.Deserializer;
import com.github.siroshun09.configapi.core.serialization.Serialization;
import com.github.siroshun09.configapi.core.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class SerializationRegistryImpl<S> extends AbstractRegistry<Serialization<?, S>> implements SerializationRegistry<S> {

    static final EmptySerializationRegistry EMPTY = new EmptySerializationRegistry();

    private final ReferenceSerializerRegistry serializerRegistry = new ReferenceSerializerRegistry();
    private final ReferenceDeserializerRegistry deserializerRegistry = new ReferenceDeserializerRegistry();

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> Serialization<T, S> get(@NotNull Class<T> clazz) {
        return (Serialization<T, S>) this.getValue(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T> SerializationRegistry<S> register(@NotNull Class<T> clazz, @NotNull Serialization<? super T, ? extends S> serialization) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(serialization);

        if (this.isFrozen()) {
            this.throwISE();
        }

        this.registerValue(clazz, (Serialization<T, S>) serialization);
        return this;
    }

    @Override
    public @NotNull SerializationRegistry<S> registerAll(@NotNull SerializationRegistry<S> registry) {
        Objects.requireNonNull(registry);

        if (this.isFrozen()) {
            this.throwISE();
        }

        if (registry instanceof EmptySerializationRegistry) {
            return this;
        }

        this.registerAll(((SerializationRegistryImpl<S>) registry).getMap());
        return this;
    }

    @Override
    public @NotNull SerializationRegistry<S> freeze() {
        this.freezeRegistry();
        return this;
    }

    @Override
    public @NotNull SerializerRegistry<S> asSerializerRegistry() {
        return this.serializerRegistry;
    }

    @Override
    public @NotNull DeserializerRegistry<S> asDeserializerRegistry() {
        return this.deserializerRegistry;
    }

    final class ReferenceSerializerRegistry implements SerializerRegistry<S> {

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable <T> Serializer<T, S> get(@NotNull Class<T> clazz) {
            var serialization = this.ref().get(clazz);
            return serialization != null && serialization.hasSerializer() ? (Serializer<T, S>) serialization.serializer() : null;
        }

        @Override
        public @NotNull <T> SerializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Serializer<? super T, ? extends S> serializer) {
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(serializer);

            if (this.ref().isFrozen()) {
                this.ref().throwISE();
            }

            var existing = this.ref().get(clazz);

            if (existing != null && existing.hasDeserializer()) {
                this.ref().register(clazz, Serialization.create(serializer, existing.deserializer()));
            } else {
                this.ref().register(clazz, Serialization.onlySerializer(serializer));
            }

            return this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public @NotNull SerializerRegistry<S> registerAll(@NotNull SerializerRegistry<S> registry) {
            Objects.requireNonNull(registry);

            if (this.ref().isFrozen()) {
                this.ref().throwISE();
            }

            if (registry instanceof SerializerRegistryImpl.EmptySerializerRegistry) {
                return this;
            }

            if (registry instanceof ReferenceSerializerRegistry other) {
                this.ref().registerAll(other.ref());
            } else if (registry instanceof SerializerRegistryImpl<S> impl) {
                var map = impl.getMap();
                var newMap = new HashMap<Class<?>, Serialization<?, S>>(map.size(), 1.0f);

                for (Map.Entry<Class<?>, Serializer<?, S>> entry : map.entrySet()) {
                    var clazz = entry.getKey();
                    var serializer = entry.getValue();
                    var existing = this.ref().get(clazz);

                    if (existing != null && existing.hasDeserializer()) {
                        newMap.put(clazz, Serialization.create(serializer, (Deserializer) existing.deserializer()));
                    } else {
                        newMap.put(clazz, Serialization.onlySerializer(serializer));
                    }
                }

                this.ref().registerAll(newMap);
            } else {
                throw new IllegalArgumentException("Unsupported registry impl: " + registry.getClass().getName());
            }

            return this;
        }

        @Override
        public @NotNull SerializerRegistry<S> freeze() {
            this.ref().freeze();
            return this;
        }

        @Override
        public boolean isFrozen() {
            return this.ref().isFrozen();
        }

        @NotNull SerializationRegistryImpl<S> ref() {
            return SerializationRegistryImpl.this;
        }
    }

    final class ReferenceDeserializerRegistry implements DeserializerRegistry<S> {

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable <T> Deserializer<S, T> get(@NotNull Class<T> clazz) {
            var serialization = this.ref().get(clazz);
            return serialization != null && serialization.hasDeserializer() ? (Deserializer<S, T>) serialization.deserializer() : null;
        }

        @Override
        public @NotNull <T> DeserializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Deserializer<? super S, ? extends T> deserializer) {
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(deserializer);

            if (this.ref().isFrozen()) {
                this.ref().throwISE();
            }

            var existing = this.ref().get(clazz);

            if (existing != null && existing.hasSerializer()) {
                this.ref().register(clazz, Serialization.create(existing.serializer(), deserializer));
            } else {
                this.ref().register(clazz, Serialization.onlyDeserializer(deserializer));
            }

            return this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public @NotNull DeserializerRegistry<S> registerAll(@NotNull DeserializerRegistry<S> registry) {
            Objects.requireNonNull(registry);

            if (this.ref().isFrozen()) {
                this.ref().throwISE();
            }

            if (registry instanceof DeserializerRegistryImpl.EmptyDeserializerRegistry) {
                return this;
            }

            if (registry instanceof ReferenceDeserializerRegistry other) {
                this.ref().registerAll(other.ref());
            } else if (registry instanceof DeserializerRegistryImpl<S> impl) {
                var map = impl.getMap();
                var newMap = new HashMap<Class<?>, Serialization<?, S>>(map.size(), 1.0f);

                for (var entry : map.entrySet()) {
                    var clazz = entry.getKey();
                    var deserializer = entry.getValue();
                    var existing = this.ref().get(clazz);

                    if (existing != null && existing.hasDeserializer()) {
                        newMap.put(clazz, Serialization.create((Serializer) existing.serializer(), deserializer));
                    } else {
                        newMap.put(clazz, Serialization.onlyDeserializer(deserializer));
                    }
                }

                this.ref().registerAll(newMap);
            } else {
                throw new IllegalArgumentException("Unsupported registry impl: " + registry.getClass().getName());
            }

            return this;
        }

        @Override
        public @NotNull DeserializerRegistry<S> freeze() {
            this.ref().freeze();
            return this;
        }

        @Override
        public boolean isFrozen() {
            return this.ref().isFrozen();
        }

        @NotNull SerializationRegistryImpl<S> ref() {
            return SerializationRegistryImpl.this;
        }
    }

    @SuppressWarnings("rawtypes")
    static final class EmptySerializationRegistry extends AbstractEmptyRegistry<Serialization, SerializationRegistry> implements SerializationRegistry {
        @Override
        public @NotNull SerializerRegistry asSerializerRegistry() {
            return SerializerRegistryImpl.EMPTY;
        }

        @Override
        public @NotNull DeserializerRegistry asDeserializerRegistry() {
            return DeserializerRegistryImpl.EMPTY;
        }
    }
}
