/*
 *     Copyright 2020 Siroshun09
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

package com.github.siroshun09.configapi.common;

import com.github.siroshun09.configapi.common.configurable.Configurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An interface that gets the value by path.
 */
public interface Configuration {

    /**
     * Gets the requested Object by path.
     * <p>
     * If the value could not be obtained, this method returns {@code null}.
     *
     * @param path Path of the Object to get.
     * @return Requested Object.
     */
    @Nullable
    Object get(@NotNull String path);

    /**
     * Gets the requested Object by path.
     *
     * @param path Path of the Object to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested Object.
     */
    @NotNull
    default Object get(@NotNull String path, @NotNull Object def) {
        Objects.requireNonNull(path, "path must not be null.");
        Objects.requireNonNull(def, "def must not be null.");

        Object value = get(path);
        return value != null ? value : def;
    }

    /**
     * Gets the requested value by {@link Configurable#getKey()}
     *
     * @param configurable The configurable to get the path.
     * @param <T>          The value type.
     * @return Requested value.
     */
    default @NotNull <T> T get(@NotNull Configurable<T> configurable) {
        Objects.requireNonNull(configurable);
        return configurable.getValue(this);
    }


    /**
     * Gets the requested value by {@link Configurable#getKey()}
     *
     * @param configurable The configurable to get the path.
     * @param <T>          The value type.
     * @return Requested value or {@code null}.
     */
    default @Nullable <T> T getOrNull(@NotNull Configurable<T> configurable) {
        Objects.requireNonNull(configurable);
        return configurable.getValueOrNull(this);
    }

    /**
     * Gets the requested boolean by path.
     * <p>
     * If the value could not be obtained, this method returns {@code false}.
     *
     * @param path Path of the boolean to get.
     * @return Requested boolean.
     */
    default boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    /**
     * Gets the requested boolean by path.
     *
     * @param path Path of the boolean to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested boolean.
     */
    boolean getBoolean(@NotNull String path, boolean def);

    /**
     * Gets the requested double by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the double to get.
     * @return Requested double.
     */
    default double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    /**
     * Gets the requested double by path.
     *
     * @param path Path of the double to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested double.
     */
    double getDouble(@NotNull String path, double def);

    /**
     * Gets the requested integer by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the integer to get.
     * @return Requested integer.
     */
    default int getInteger(@NotNull String path) {
        return getInteger(path, 0);
    }

    /**
     * Gets the requested integer by path.
     *
     * @param path Path of the integer to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested integer.
     */
    int getInteger(@NotNull String path, int def);

    /**
     * Gets the requested long by path.
     * <p>
     * If the value could not be obtained, this method returns 0.
     *
     * @param path Path of the long to get.
     * @return Requested long.
     */
    default long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    /**
     * Gets the requested long by path.
     *
     * @param path Path of the long to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested long.
     */
    long getLong(@NotNull String path, long def);

    /**
     * Gets the requested string by path.
     * <p>
     * If the value could not be obtained, this method returns an empty string.
     *
     * @param path Path of the string to get.
     * @return Requested string.
     */
    @NotNull
    default String getString(@NotNull String path) {
        return getString(path, "");
    }

    /**
     * Gets the requested string by path.
     *
     * @param path Path of the string to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested string.
     */
    @NotNull
    String getString(@NotNull String path, @NotNull String def);

    /**
     * Gets the requested string list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty string list.
     *
     * @param path Path of the string list to get.
     * @return Requested string list.
     */
    @NotNull
    default List<String> getStringList(@NotNull String path) {
        return getStringList(path, new ArrayList<>());
    }

    /**
     * Gets the requested string list by path.
     *
     * @param path Path of the string list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested string list.
     */
    @NotNull
    List<String> getStringList(@NotNull String path, @NotNull List<String> def);

    /**
     * Gets the requested short list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty short list.
     *
     * @param path Path of the short list to get.
     * @return Requested short list.
     */
    @NotNull
    default List<Short> getShortList(@NotNull String path) {
        return getShortList(path, new ArrayList<>());
    }

    /**
     * Gets the requested short list by path.
     *
     * @param path Path of the short list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested short list.
     */
    @NotNull
    List<Short> getShortList(@NotNull String path, @NotNull List<Short> def);

    /**
     * Gets the requested integer list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty integer list.
     *
     * @param path Path of the integer list to get.
     * @return Requested integer list.
     */
    @NotNull
    default List<Integer> getIntegerList(@NotNull String path) {
        return getIntegerList(path, new ArrayList<>());
    }

    /**
     * Gets the requested integer list by path.
     *
     * @param path Path of the integer list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested integer list.
     */
    @NotNull
    List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def);

    /**
     * Gets the requested long list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty long list.
     *
     * @param path Path of the long list to get.
     * @return Requested long list.
     */
    @NotNull
    default List<Long> getLongList(@NotNull String path) {
        return getLongList(path, new ArrayList<>());
    }

    /**
     * Gets the requested long list by path.
     *
     * @param path Path of the long list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested long list.
     */
    @NotNull
    List<Long> getLongList(@NotNull String path, @NotNull List<Long> def);

    /**
     * Gets the requested float list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty float list.
     *
     * @param path Path of the float list to get.
     * @return Requested float list.
     */
    @NotNull
    default List<Float> getFloatList(@NotNull String path) {
        return getFloatList(path, new ArrayList<>());
    }

    /**
     * Gets the requested float list by path.
     *
     * @param path Path of the float list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested float list.
     */
    @NotNull
    List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def);

    /**
     * Gets the requested double list by path.
     * <p>
     * If the value could not be obtained, this method returns an empty double list.
     *
     * @param path Path of the double list to get.
     * @return Requested double list.
     */
    @NotNull
    default List<Double> getDoubleList(@NotNull String path) {
        return getDoubleList(path, new ArrayList<>());
    }

    /**
     * Gets the requested double list by path.
     *
     * @param path Path of the double list to get.
     * @param def  The default list to return if the value could not be obtained.
     * @return Requested double list.
     */
    @NotNull
    List<Double> getDoubleList(@NotNull String path, @NotNull List<Double> def);

    /**
     * Gets a set containing keys in this yaml file.
     * <p>
     * The returned set does not include deep key.
     *
     * @return Set of keys contained within this yaml file.
     */
    @NotNull
    Collection<String> getKeys();

    /**
     * Set the value to the specified path.
     * <p>
     * If given value is null, the path will be removed.
     *
     * @param path  Path of the object to set.
     * @param value New value to set the path to.
     */
    void set(@NotNull String path, @Nullable Object value);

    /**
     * Sets the default value to the specified path.
     *
     * @param configurable The configurable to get the path and the default value.
     */
    default void setDefault(@NotNull Configurable<?> configurable) {
        Objects.requireNonNull(configurable);
        set(configurable.getKey(), configurable.getDefault());
    }


    /**
     * Sets the default values to the specified path.
     *
     * @param configurableIterator The configurable to get the path and the default value.
     */
    default void setDefault(@NotNull Iterator<Configurable<?>> configurableIterator) {
        Objects.requireNonNull(configurableIterator).forEachRemaining(this::setDefault);
    }
}
