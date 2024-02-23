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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

final class DeserializerRegistryImpl<S> extends AbstractRegistry<Deserializer<S, ?>> implements DeserializerRegistry<S> {

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> Deserializer<S, T> get(@NotNull Class<T> clazz) {
        return (Deserializer<S, T>) this.getValue(Objects.requireNonNull(clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T> DeserializerRegistry<S> register(@NotNull Class<T> clazz, @NotNull Deserializer<? super S, ? extends T> deserializer) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(deserializer);

        if (this.isFrozen()) {
            this.throwISE();
        }

        this.registerValue(clazz, (Deserializer<S, T>) deserializer);
        return this;
    }

    @Override
    public @NotNull DeserializerRegistry<S> registerAll(@NotNull DeserializerRegistry<S> registry) {
        Objects.requireNonNull(registry);

        if (this.isFrozen()) {
            this.throwISE();
        }

        if (registry instanceof SerializationRegistryImpl<S>.ReferenceDeserializerRegistry other) {
            var map = other.ref().getMap();
            var newMap = new HashMap<Class<?>, Deserializer<S, ?>>(map.size(), 1.0f);

            for (var entry : map.entrySet()) {
                if (entry.getValue().hasDeserializer()) {
                    newMap.put(entry.getKey(), entry.getValue().deserializer());
                }
            }

            this.registerAll(newMap);
        } else if (registry instanceof DeserializerRegistryImpl<S> impl) {
            this.registerAll(impl.getMap());
        } else {
            throw new IllegalArgumentException("Unsupported registry impl: " + registry.getClass().getName());
        }

        return this;
    }

    @Override
    public @NotNull DeserializerRegistry<S> freeze() {
        this.freezeRegistry();
        return this;
    }
}
