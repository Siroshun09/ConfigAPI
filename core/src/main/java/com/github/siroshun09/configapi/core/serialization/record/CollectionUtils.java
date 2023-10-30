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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class CollectionUtils {

    static @Nullable Collection<Object> emptyCollectionOrNull(@NotNull Class<?> clazz) {
        if (clazz == List.class || clazz == Collection.class) {
            return Collections.emptyList();
        } else if (clazz == Set.class) {
            return Collections.emptySet();
        } else {
            return null;
        }
    }

    static @Nullable Collection<Object> createCollection(@NotNull Class<?> clazz, int size) {
        if (size < 1) {
            return emptyCollectionOrNull(clazz);
        }

        if (clazz == List.class || clazz == Collection.class) {
            return new ArrayList<>(size);
        } else if (clazz == Set.class) {
            return new HashSet<>(size, 1.0f);
        } else {
            return null;
        }
    }

    static @NotNull Collection<Object> unmodifiable(@NotNull Class<?> clazz, @NotNull Collection<Object> collection) {
        if (clazz == List.class) {
            return Collections.unmodifiableList((List<?>) collection);
        } else if (clazz == Set.class) {
            return Collections.unmodifiableSet((Set<?>) collection);
        } else {
            return Collections.unmodifiableCollection(collection);
        }
    }

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }
}
