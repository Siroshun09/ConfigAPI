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

package dev.siroshun.configapi.core.serialization.registry;

import dev.siroshun.configapi.core.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

final class SerializerRegistryImpl<S> extends AbstractRegistry<Serializer<?, S>> implements SerializerRegistry<S> {

    static final EmptySerializerRegistry EMPTY = new EmptySerializerRegistry();

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> Serializer<T, S> get(@NotNull Class<T> clazz) {
        return (Serializer<T, S>) this.getValue(Objects.requireNonNull(clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T> SerializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Serializer<? super T, ? extends S> serializer) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(serializer);

        if (this.isFrozen()) {
            this.throwISE();
        }

        this.registerValue(clazz, (Serializer<T, S>) serializer);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull SerializerRegistry<S> registerAll(@NotNull SerializerRegistry<S> registry) {
        Objects.requireNonNull(registry);

        if (this.isFrozen()) {
            this.throwISE();
        }

        if (registry instanceof EmptySerializerRegistry) {
            return this;
        }

        if (registry instanceof SerializationRegistryImpl<S>.ReferenceSerializerRegistry other) {
            var map = other.ref().getMap();
            var newMap = new HashMap<Class<?>, Serializer<?, S>>(map.size(), 1.0f);

            for (var entry : map.entrySet()) {
                if (entry.getValue().hasSerializer()) {
                    newMap.put(entry.getKey(), (Serializer<?, S>) entry.getValue().serializer());
                }
            }

            this.registerAll(newMap);
        } else if (registry instanceof SerializerRegistryImpl<S> impl) {
            this.registerAll(impl.getMap());
        } else {
            throw new IllegalArgumentException("Unsupported registry impl: " + registry.getClass().getName());
        }

        return this;
    }

    @Override
    public @NotNull SerializerRegistry<S> freeze() {
        this.freezeRegistry();
        return this;
    }

    @SuppressWarnings("rawtypes")
    static final class EmptySerializerRegistry extends AbstractEmptyRegistry<Serializer, SerializerRegistry> implements SerializerRegistry {
    }
}
