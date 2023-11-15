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
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultMapKey;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultNull;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultShort;
import com.github.siroshun09.configapi.core.serialization.annotation.DefaultString;
import com.github.siroshun09.configapi.core.serialization.annotation.Inline;
import com.github.siroshun09.configapi.core.serialization.annotation.MapType;
import com.github.siroshun09.configapi.core.serialization.key.Key;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
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

        var byAnnotation = getDefaultValueByAnnotation(component.getType(), component);

        if (byAnnotation != null) {
            return byAnnotation;
        }

        return createDefaultValue(component.getType(), component.isAnnotationPresent(DefaultNull.class));
    }

    static Object getDefaultValueByAnnotation(@NotNull Class<?> clazz, @NotNull RecordComponent component) {
        if (clazz == String.class) {
            var annotation = component.getDeclaredAnnotation(DefaultString.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            var annotation = component.getDeclaredAnnotation(DefaultBoolean.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == byte.class || clazz == Byte.class) {
            var annotation = component.getDeclaredAnnotation(DefaultByte.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == double.class || clazz == Double.class) {
            var annotation = component.getDeclaredAnnotation(DefaultDouble.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == float.class || clazz == Float.class) {
            var annotation = component.getDeclaredAnnotation(DefaultFloat.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == int.class || clazz == Integer.class) {
            var annotation = component.getDeclaredAnnotation(DefaultInt.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == long.class || clazz == Long.class) {
            var annotation = component.getDeclaredAnnotation(DefaultLong.class);
            return annotation != null ? annotation.value() : null;
        } else if (clazz == short.class || clazz == Short.class) {
            var annotation = component.getDeclaredAnnotation(DefaultShort.class);
            return annotation != null ? annotation.value() : null;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            var annotation = component.getDeclaredAnnotation(DefaultEnum.class);
            return annotation != null ? Enum.valueOf(clazz.asSubclass(Enum.class), annotation.value()) : null;
        } else {
            return null;
        }
    }

    static Object createDefaultValue(@NotNull Class<?> clazz, boolean defaultNull) {
        if (clazz == String.class) {
            return defaultNull ? null : "";
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return false;
        } else if (clazz == byte.class || clazz == Byte.class ||
                clazz == double.class || clazz == Double.class ||
                clazz == float.class || clazz == Float.class ||
                clazz == int.class || clazz == Integer.class ||
                clazz == long.class || clazz == Long.class ||
                clazz == short.class || clazz == Short.class) {
            return defaultNull && !clazz.isPrimitive() ? null : 0;
        } else if (clazz.isRecord()) {
            return defaultNull ? null : createDefaultRecord(clazz);
        } else if (CollectionUtils.isSupportedCollectionType(clazz)) {
            return defaultNull ? null : CollectionUtils.emptyCollectionOrNull(clazz);
        } else if (clazz == Map.class) {
            return defaultNull ? null : Collections.emptyMap();
        } else if (clazz.isArray()) {
            return defaultNull ? null : Array.newInstance(clazz.getComponentType(), 0);
        } else {
            return null;
        }
    }

    static @NotNull Record createDefaultRecord(@NotNull Class<?> clazz) {
        var components = clazz.getRecordComponents();
        var types = new Class<?>[components.length];
        var args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            var component = components[i];
            var type = component.getType();

            Object arg;

            if (CollectionUtils.isSupportedCollectionType(type)) {
                arg = CollectionUtils.emptyCollectionOrNull(type);
            } else if (type == Map.class) {
                arg = createDefaultMap(component);
            } else if (type.isArray()) {
                arg = Array.newInstance(type.getComponentType(), 0);
            } else if (type.isRecord()) {
                if (component.isAnnotationPresent(Inline.class)) {
                    arg = createInlinedRecord(component, type);
                } else {
                    arg = component.isAnnotationPresent(DefaultNull.class) ? null : createDefaultRecord(type);
                }
            } else {
                arg = getDefaultValue(component, null);
            }

            types[i] = type;
            args[i] = arg;
        }

        return createRecord(clazz.asSubclass(Record.class), types, args);
    }

    static <R> @NotNull R createInlinedRecord(@NotNull RecordComponent parent, @NotNull Class<R> clazz) {
        var recordComponents = parent.getType().getRecordComponents();

        if (recordComponents.length != 1) {
            throw new SerializationException("The component of the record for which @Inline is specified must be one.");
        }

        var inlinedComponent = recordComponents[0];
        var type = inlinedComponent.getType();

        var defaultValue = RecordUtils.getDefaultValueByAnnotation(type, parent);
        if (defaultValue == null)
            defaultValue = RecordUtils.getDefaultValueByAnnotation(type, inlinedComponent);
        if (defaultValue == null)
            defaultValue = RecordUtils.createDefaultValue(type, inlinedComponent.isAnnotationPresent(DefaultNull.class));

        return createRecord(clazz, new Class[]{type}, new Object[]{defaultValue});
    }

    static <R> @NotNull R createRecord(@NotNull Class<R> clazz, @NotNull Class<?> @NotNull [] types, Object @NotNull [] args) {
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

    static Object createDefaultMap(@NotNull RecordComponent component) {
        var mapType = component.getDeclaredAnnotation(MapType.class);
        var defaultMapKey = component.getDeclaredAnnotation(DefaultMapKey.class);

        if (mapType == null || mapType.key() != String.class || defaultMapKey == null) {
            return Collections.emptyMap();
        }

        var defaultValue = createDefaultValue(mapType.value(), false);
        return defaultValue != null ? Map.of(defaultMapKey.value(), defaultValue) : Collections.emptyMap();
    }
}
