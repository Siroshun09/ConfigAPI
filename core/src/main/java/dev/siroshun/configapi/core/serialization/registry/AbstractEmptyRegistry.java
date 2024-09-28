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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
abstract class AbstractEmptyRegistry<T, C> {

    public @Nullable T get(@NotNull Class clazz) {
        return null;
    }

    public @NotNull C register(@NotNull Class clazz, @NotNull T serializer) {
        throw new IllegalStateException("This registry is frozen.");
    }

    public @NotNull C registerAll(@NotNull C registry) {
        throw new IllegalStateException("This registry is frozen.");
    }

    @SuppressWarnings("unchecked")
    public @NotNull C freeze() {
        return (C) this;
    }

    public boolean isFrozen() {
        return true;
    }
}
