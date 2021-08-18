/*
 *     Copyright 2021 Siroshun09
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

package com.github.siroshun09.configapi.api;

import com.github.siroshun09.configapi.api.serializer.Serializer;
import com.github.siroshun09.configapi.api.value.ConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An abstract implementation of {@link Configuration}.
 */
public abstract class AbstractConfiguration implements Configuration {

    @Override
    public @NotNull Object get(@NotNull String path, @NotNull Object def) {
        var value = get(path);
        return value != null ? value : Objects.requireNonNull(def);
    }

    @Override
    public <T> @Nullable T get(@NotNull String path, @NotNull Serializer<T, ?> serializer) {
        var value = get(path);
        return value != null ? serializer.deserialize(value) : null;
    }

    @Override
    public <T> @NotNull T get(@NotNull String path, @NotNull Serializer<T, ?> serializer, @NotNull T def) {
        var value = get(path, serializer);
        return value != null ? value : def;
    }

    @Override
    public <T> @NotNull T get(@NotNull ConfigValue<T> configValue) {
        return configValue.get(this);
    }

    @Override
    public <T> void set(@NotNull String path, @NotNull T value, @NotNull Serializer<T, ?> serializer) {
        set(path, serializer.serialize(value));
    }

    @Override
    public @NotNull @Unmodifiable List<?> getList(@NotNull String path) {
        return getList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<?> getList(@NotNull String path, @NotNull List<?> def) {
        var value = get(path);
        return value instanceof List<?> ? (List<?>) value : Objects.requireNonNull(def);
    }

    @Override
    public @NotNull @Unmodifiable <T> List<T> getList(@NotNull String path, @NotNull Serializer<T, ?> serializer) {
        return getList(path).stream()
                .map(serializer::deserialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NotNull @Unmodifiable <T> List<T> getList(@NotNull String path, @NotNull Serializer<T, ?> serializer, List<T> def) {
        var list = getListOrNull(path);

        if (list != null) {
            return list.stream()
                    .map(serializer::deserialize)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public <T> void setList(@NotNull String path, @NotNull List<T> list, @NotNull Serializer<T, ?> serializer) {
        set(path, list.stream().map(serializer::serialize).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        var value = get(path);
        return value instanceof Boolean ? (Boolean) value : def;
    }

    @Override
    public byte getByte(@NotNull String path) {
        return getByte(path, (byte) 0);
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        return getNumber(path, def, Number::byteValue);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return getNumber(path, def, Number::doubleValue);
    }

    @Override
    public float getFloat(@NotNull String path) {
        return getFloat(path, 0);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        return getNumber(path, def, Number::floatValue);
    }

    @Override
    public int getInteger(@NotNull String path) {
        return getInteger(path, 0);
    }

    @Override
    public int getInteger(@NotNull String path, int def) {
        return getNumber(path, def, Number::intValue);
    }

    @Override
    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return getNumber(path, def, Number::longValue);
    }

    @Override
    public short getShort(@NotNull String path) {
        return getShort(path, (short) 0);
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        return getNumber(path, def, Number::shortValue);
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        return getString(path, "");
    }

    @Override
    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        var value = get(path);

        if (value != null) {
            return value instanceof String ? (String) value : value.toString();
        } else {
            return Objects.requireNonNull(def);
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Boolean> getBooleanList(@NotNull String path) {
        return getBooleanList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Boolean> getBooleanList(@NotNull String path, @NotNull List<Boolean> def) {
        var list = getListOrNull(path);

        if (list != null) {
            return list.stream()
                    .filter(object -> object instanceof Boolean)
                    .map(object -> (Boolean) object)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Byte> getByteList(@NotNull String path) {
        return getByteList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Byte> getByteList(@NotNull String path, @NotNull List<Byte> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::byteValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Double> getDoubleList(@NotNull String path) {
        return getDoubleList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Double> getDoubleList(@NotNull String path, @NotNull List<Double> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::doubleValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Float> getFloatList(@NotNull String path) {
        return getFloatList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::floatValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Integer> getIntegerList(@NotNull String path) {
        return getIntegerList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::intValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Long> getLongList(@NotNull String path) {
        return getLongList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Long> getLongList(@NotNull String path, @NotNull List<Long> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::longValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<Short> getShortList(@NotNull String path) {
        return getShortList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<Short> getShortList(@NotNull String path, @NotNull List<Short> def) {
        var stream = getNumberStreamOrNull(path);

        if (stream != null) {
            return stream.map(Number::shortValue).collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String path) {
        return getStringList(path, Collections.emptyList());
    }

    @Override
    public @NotNull @Unmodifiable List<String> getStringList(@NotNull String path, @NotNull List<String> def) {
        var list = getListOrNull(path);

        if (list != null) {
            return list.stream()
                    .map(object -> object instanceof String ? (String) object : object.toString())
                    .collect(Collectors.toUnmodifiableList());
        } else {
            return def;
        }
    }

    @Override
    public byte[] getBytes(@NotNull String path) {
        var str = getString(path);

        if (str.isEmpty()) {
            return new byte[0];
        }

        try {
            return Base64.getDecoder().decode(str);
        } catch (IllegalArgumentException ignored) {
            return new byte[0];
        }
    }

    @Override
    public void setBytes(@NotNull String path, byte[] bytes) {
        set(path, Base64.getEncoder().encodeToString(bytes));
    }

    private <T extends Number> @NotNull T getNumber(@NotNull String path, T def,
                                                    @NotNull Function<Number, T> converter) {
        var value = get(path);
        return value instanceof Number ? converter.apply((Number) value) : def;
    }

    private @Nullable List<?> getListOrNull(@NotNull String path) {
        var value = get(path);
        return value instanceof List<?> ? (List<?>) value : null;
    }

    private @Nullable Stream<Number> getNumberStreamOrNull(@NotNull String path) {
        var list = getListOrNull(path);

        if (list != null) {
            return list.stream()
                    .filter(object -> object instanceof Number)
                    .map(object -> (Number) object);
        } else {
            return null;
        }
    }
}
