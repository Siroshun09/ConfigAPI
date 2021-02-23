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

package com.github.siroshun09.configapi.common.defaultvalue;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class DefaultValueFactory {

    private DefaultValueFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Creates {@link BooleanValue}.
     *
     * @param path The path
     * @param def  The default boolean value
     * @return {@link BooleanValue}
     */
    public static @NotNull BooleanValue create(@NotNull String path, boolean def) {
        return new BooleanValue(path, def);
    }

    /**
     * Creates {@link ByteValue}.
     *
     * @param path The path
     * @param def  The default byte value
     * @return {@link ByteValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull ByteValue create(@NotNull String path, byte def) {
        return new ByteValue(path, def);
    }

    /**
     * Creates {@link DoubleValue}.
     *
     * @param path The path
     * @param def  The default double value
     * @return {@link DoubleValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull DoubleValue create(@NotNull String path, double def) {
        return new DoubleValue(path, def);
    }

    /**
     * Creates {@link FloatValue}.
     *
     * @param path The path
     * @param def  The default float value
     * @return {@link FloatValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull FloatValue create(@NotNull String path, float def) {
        return new FloatValue(path, def);
    }

    /**
     * Creates {@link IntegerValue}.
     *
     * @param path The path
     * @param def  The default integer value
     * @return {@link IntegerValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull IntegerValue create(@NotNull String path, int def) {
        return new IntegerValue(path, def);
    }

    /**
     * Creates {@link LongValue}.
     *
     * @param path The path
     * @param def  The default long value
     * @return {@link LongValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull LongValue create(@NotNull String path, long def) {
        return new LongValue(path, def);
    }

    /**
     * Creates {@link StringValue}.
     *
     * @param path The path
     * @param def  The default string value
     * @return {@link StringValue}
     */
    @Contract("_, _ -> new")
    public static @NotNull StringValue create(@NotNull String path, @NotNull String def) {
        return new StringValue(path, def);
    }

    /**
     * Creates {@link BooleanList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link BooleanList}
     */
    public static @NotNull BooleanList createBooleanList(@NotNull String path) {
        return createBooleanList(path, Collections.emptyList());
    }

    /**
     * Creates {@link BooleanList}.
     *
     * @param path The path
     * @param def  The default byte list
     * @return {@link BooleanList}
     */
    @Contract("_, _ -> new")
    public static @NotNull BooleanList createBooleanList(@NotNull String path, @NotNull List<Boolean> def) {
        return new BooleanList(path, def);
    }

    /**
     * Creates {@link ByteList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link ByteList}
     */
    public static @NotNull ByteList createByteList(@NotNull String path) {
        return createByteList(path, Collections.emptyList());
    }

    /**
     * Creates {@link ByteList}.
     *
     * @param path The path
     * @param def  The default byte list
     * @return {@link ByteList}
     */
    @Contract("_, _ -> new")
    public static @NotNull ByteList createByteList(@NotNull String path, @NotNull List<Byte> def) {
        return new ByteList(path, def);
    }

    /**
     * Creates {@link DoubleList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link DoubleList}
     */
    public static @NotNull DoubleList createDoubleList(@NotNull String path) {
        return createDoubleList(path, Collections.emptyList());
    }

    /**
     * Creates {@link DoubleList}.
     *
     * @param path The path
     * @param def  The default double list
     * @return {@link DoubleList}
     */
    @Contract("_, _ -> new")
    public static @NotNull DoubleList createDoubleList(@NotNull String path, @NotNull List<Double> def) {
        return new DoubleList(path, def);
    }

    /**
     * Creates {@link FloatList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link FloatList}
     */
    public static @NotNull FloatList createFloatList(@NotNull String path) {
        return createFloatList(path, Collections.emptyList());
    }

    /**
     * Creates {@link FloatList}.
     *
     * @param path The path
     * @param def  The default float list
     * @return {@link FloatList}
     */
    @Contract("_, _ -> new")
    public static @NotNull FloatList createFloatList(@NotNull String path, @NotNull List<Float> def) {
        return new FloatList(path, def);
    }

    /**
     * Creates {@link IntegerList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link IntegerList}
     */
    public static @NotNull IntegerList createIntegerList(@NotNull String path) {
        return createIntegerList(path, Collections.emptyList());
    }

    /**
     * Creates {@link IntegerList}.
     *
     * @param path The path
     * @param def  The default integer list
     * @return {@link IntegerList}
     */
    @Contract("_, _ -> new")
    public static @NotNull IntegerList createIntegerList(@NotNull String path, @NotNull List<Integer> def) {
        return new IntegerList(path, def);
    }

    /**
     * Creates {@link LongList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link LongList}
     */
    public static @NotNull LongList createLongList(@NotNull String path) {
        return createLongList(path, Collections.emptyList());
    }

    /**
     * Creates {@link LongList}.
     *
     * @param path The path
     * @param def  The default long list
     * @return {@link LongList}
     */
    @Contract("_, _ -> new")
    public static @NotNull LongList createLongList(@NotNull String path, @NotNull List<Long> def) {
        return new LongList(path, def);
    }

    /**
     * Creates {@link StringList}.
     * <p>
     * Default list is {@link Collections#emptyList()}.
     *
     * @param path The path
     * @return {@link StringList}
     */
    public static @NotNull StringList createStringList(@NotNull String path) {
        return createStringList(path, Collections.emptyList());
    }

    /**
     * Creates {@link StringList}.
     *
     * @param path The path
     * @param def  The default string list
     * @return {@link StringList}
     */
    @Contract("_, _ -> new")
    public static @NotNull StringList createStringList(@NotNull String path, @NotNull List<String> def) {
        return new StringList(path, def);
    }
}
