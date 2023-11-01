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

import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultBoolean;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultByte;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultDouble;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultEnum;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultFloat;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultInt;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultLong;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultNull;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultShort;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultString;
import com.github.siroshun09.configapi.core.serialization.key.Key;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

final class RecordUtils {

    static @NotNull String getKey(@NotNull RecordComponent component, @NotNull KeyGenerator keyGenerator) {
        var annotation = component.getDeclaredAnnotation(Key.class);

        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        } else {
            return keyGenerator.generate(component.getName());
        }
    }

    static @UnknownNullability Object getValue(@NotNull RecordComponent component, @NotNull Record record) {
        try {
            var accessor = component.getAccessor();
            accessor.setAccessible(true);
            return accessor.invoke(record);
        } catch (IllegalAccessException | InvocationTargetException cause) {
            throw new SerializationException(
                    "Failed to get the value (" + record.getClass().getName() + "#" + component.getName() + ")",
                    cause
            );
        }
    }

    static @Nullable Object getDefaultValue(@NotNull RecordComponent component, @Nullable Record defaultRecord) {
        if (defaultRecord != null) {
            return RecordUtils.getValue(component, defaultRecord);
        }

        var clazz = component.getType();

        if (clazz == String.class) {
            var annotation = component.getDeclaredAnnotation(DefaultString.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return hasDefaultNullAnnotation(component) ? null : "";
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            var annotation = component.getDeclaredAnnotation(DefaultBoolean.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Boolean.class && hasDefaultNullAnnotation(component) ? null : false;
            }
        } else if (clazz == byte.class || clazz == Byte.class) {
            var annotation = component.getDeclaredAnnotation(DefaultByte.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Byte.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (clazz == double.class || clazz == Double.class) {
            var annotation = component.getDeclaredAnnotation(DefaultDouble.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Double.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (clazz == float.class || clazz == Float.class) {
            var annotation = component.getDeclaredAnnotation(DefaultFloat.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Float.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (clazz == int.class || clazz == Integer.class) {
            var annotation = component.getDeclaredAnnotation(DefaultInt.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Integer.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (clazz == long.class || clazz == Long.class) {
            var annotation = component.getDeclaredAnnotation(DefaultLong.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Long.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (clazz == short.class || clazz == Short.class) {
            var annotation = component.getDeclaredAnnotation(DefaultShort.class);
            if (annotation != null) {
                return annotation.value();
            } else {
                return clazz == Short.class && hasDefaultNullAnnotation(component) ? null : 0;
            }
        } else if (Enum.class.isAssignableFrom(clazz)) {
            var annotation = component.getDeclaredAnnotation(DefaultEnum.class);
            return annotation != null ? Enum.valueOf(clazz.asSubclass(Enum.class), annotation.value()) : null;
        } else if (clazz.isRecord()) {
            return hasDefaultNullAnnotation(component) ? null : createDefaultRecord(clazz);
        }

        return null;
    }

    static @NotNull Record createDefaultRecord(@NotNull Class<?> clazz) {
        var components = clazz.getRecordComponents();
        var types = new Class<?>[components.length];
        var args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            var component = components[i];
            var type = component.getType();

            Object arg;

            if (Collection.class.isAssignableFrom(type)) {
                arg = CollectionUtils.emptyCollectionOrNull(type);
            } else if (type == Map.class) {
                arg = Collections.emptyMap();
            } else {
                arg = getDefaultValue(component, null);
            }

            types[i] = type;
            args[i] = arg;
        }

        return createRecord(clazz.asSubclass(Record.class), types, args);
    }

    static <R> @NotNull R createRecord(@NotNull Class<R> clazz, @NotNull Class<?> @NotNull [] types, @NotNull Object[] args) {
        try {
            var constructor = clazz.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new SerializationException(e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException("Could not create " + clazz.getName() + " instance.", e);
        }
    }

    private static boolean hasDefaultNullAnnotation(@NotNull RecordComponent component) {
        return component.getDeclaredAnnotation(DefaultNull.class) != null;
    }
}
