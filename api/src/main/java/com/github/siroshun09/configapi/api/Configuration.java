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

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An interface that can get a value from a path.
 */
public interface Configuration {

    /**
     * The separator to split paths.
     */
    char PATH_SEPARATOR = '.';

    /**
     * The string representation of {@link Configuration#PATH_SEPARATOR}.
     */
    String PATH_SEPARATOR_STRING = String.valueOf(PATH_SEPARATOR);

    /**
     * Gets the object of the specified path.
     * <p>
     * If the object could not be obtained, this method returns {@code null}.
     *
     * @param path the path to get the object
     * @return the object or {@code null}
     */
    @Nullable Object get(@NotNull String path);

    /**
     * Gets the object of the specified path.
     * <p>
     * If the object could not be obtained or deserialized,
     * this method returns {@code null}.
     *
     * @param path       the path to get the object
     * @param serializer the serializer to deserialize object
     * @param <T>        the type of value
     * @return the deserialized object or {@code null}
     */
    <T> @Nullable T get(@NotNull String path, @NotNull Serializer<T, ?> serializer);

    /**
     * Gets the object of the specified path.
     *
     * @param path the path to get the object
     * @param def  the default object to return if the object could not be obtained
     * @return the object
     */
    @NotNull Object get(@NotNull String path, @NotNull Object def);

    /**
     * Gets the object of the specified path.
     * <p>
     * The object to be returned will be deserialized using {@link Serializer}.
     *
     * @param path       the path to get the object
     * @param serializer the serializer to deserialize the object
     * @param def        the default object to return if the object could not be obtained or deserialized
     * @param <T>        the type of value
     * @return the deserialized object or default object
     */
    <T> @NotNull T get(@NotNull String path, @NotNull Serializer<T, ?> serializer, @NotNull T def);

    /**
     * Gets the value using {@link ConfigValue}.
     *
     * @param configValue the {@link ConfigValue} to get
     * @param <T>         the type of value
     * @return the object
     */
    <T> @NotNull T get(@NotNull ConfigValue<T> configValue);

    /**
     * Sets the object to the specified path.
     * <p>
     * If given object is {@code null},
     * the current object of the path will be removed.
     *
     * @param path  the path to set or remove
     * @param value the object to set or {@code null}
     */
    void set(@NotNull String path, @Nullable Object value);

    /**
     * Sets the object to the specified path.
     * <p>
     * The object to set will be serialized using {@link Serializer}
     *
     * @param path       the path to set
     * @param value      the value to set
     * @param serializer the serializer to serialize value
     * @param <T>        the type of value
     */
    <T> void set(@NotNull String path, @NotNull T value, @NotNull Serializer<T, ?> serializer);

    /**
     * Gets the list of root keys included in this {@link Configuration}.
     * <p>
     * This method may not return to the correct order depending on the implementation.
     *
     * @return list of root keys
     */
    @NotNull @Unmodifiable List<String> getKeyList();

    /**
     * Gets the set of root objects included in this {@link Configuration}.
     *
     * @return set of root keys
     */
    @NotNull @Unmodifiable Set<Object> getValues();

    /**
     * Gets the {@link Configuration} section of the specified path.
     * <p>
     * If the {@link Configuration} section could not be obtained,
     * this method returns {@code null}.
     *
     * @param path the path to get the {@link Configuration} section
     * @return the {@link Configuration} section or {@code null}
     */
    @Nullable Configuration getSection(@NotNull String path);

    /**
     * Gets the list of the specified path.
     * <p>
     * If the list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     *
     * @param path the path to get the list
     * @return the list
     */
    @NotNull @Unmodifiable List<?> getList(@NotNull String path);

    /**
     * Gets the list of the specified path.
     *
     * @param path the path to get the list
     * @param def  the default list to return if the list could not be obtained
     * @return the list or default one
     */
    @NotNull @Unmodifiable List<?> getList(@NotNull String path, @NotNull List<?> def);

    /**
     * Gets the list of the specified path.
     * <p>
     * Each element in the list is converted by the {@link Serializer},
     * and those that cannot be converted are removed from the returning list.
     * <p>
     * The list stored in this configuration is not changed.
     *
     * @param path       the path to get the list
     * @param serializer the serializer to serialize elements
     * @param <T>        the type of value
     * @return the serialized list
     */
    @NotNull @Unmodifiable <T> List<T> getList(@NotNull String path, @NotNull Serializer<T, ?> serializer);

    /**
     * Gets the list of the specified path.
     * <p>
     * Each element in the list is converted by the {@link Serializer},
     * and those that cannot be converted are removed from the list to be returned.
     * <p>
     * The list stored in this configuration will not be changed.
     *
     * @param path       the path to get the list
     * @param serializer the serializer to serialize elements
     * @param def        the default list to return if the list could not be obtained
     * @param <T>        the type of value
     * @return the serialized list or default one
     */
    @NotNull @Unmodifiable <T> List<T> getList(@NotNull String path,
                                               @NotNull Serializer<T, ?> serializer, List<T> def);

    /**
     * Sets the list to specified path.
     * <p>
     * Each element in the list is converted by the {@link Serializer},
     * and those that cannot be converted are removed from the list to be set.
     * <p>
     * The list given as an argument will not be changed.
     *
     * @param path       the path to set the list
     * @param list       the list to set
     * @param serializer the serializer to serialize elements
     * @param <T>        the type of value
     */
    <T> void setList(@NotNull String path, @NotNull List<T> list, @NotNull Serializer<T, ?> serializer);

    /**
     * Gets the boolean of the specified path.
     * <p>
     * If the boolean could not be obtained, this method returns {@code false}.
     *
     * @param path the path to get the boolean
     * @return the boolean
     */
    boolean getBoolean(@NotNull String path);

    /**
     * Gets the boolean of the specified path.
     *
     * @param path the path to get the boolean
     * @param def  the default boolean to return if the boolean could not be obtained
     * @return the boolean
     */
    boolean getBoolean(@NotNull String path, boolean def);

    /**
     * Gets the byte of the specified path.
     * <p>
     * If the byte could not be obtained, this method returns 0.
     *
     * @param path the path to get the byte
     * @return the byte
     */
    byte getByte(@NotNull String path);

    /**
     * Gets the byte of the specified path.
     *
     * @param path the path to get the byte
     * @param def  the default byte to return if the byte could not be obtained
     * @return the byte
     */
    byte getByte(@NotNull String path, byte def);

    /**
     * Gets the double of the specified path.
     * <p>
     * If the double could not be obtained, this method returns 0.
     *
     * @param path the path to get the double
     * @return the double
     */
    double getDouble(@NotNull String path);

    /**
     * Gets the double of the specified path.
     *
     * @param path the path to get the double
     * @param def  the default double to return if the double could not be obtained
     * @return the double
     */
    double getDouble(@NotNull String path, double def);

    /**
     * Gets the float of the specified path.
     * <p>
     * If the float could not be obtained, this method returns 0.
     *
     * @param path the path to get the float
     * @return the float
     */
    float getFloat(@NotNull String path);

    /**
     * Gets the float of the specified path.
     *
     * @param path the path to get the float
     * @param def  the default float to return if the float could not be obtained
     * @return the float
     */
    float getFloat(@NotNull String path, float def);

    /**
     * Gets the integer of the specified path.
     * <p>
     * If the integer could not be obtained, this method returns 0.
     *
     * @param path the path to get the integer
     * @return the integer
     */
    int getInteger(@NotNull String path);

    /**
     * Gets the integer of the specified path.
     *
     * @param path the path to get the integer
     * @param def  the default integer to return if the integer could not be obtained
     * @return the integer
     */
    int getInteger(@NotNull String path, int def);

    /**
     * Gets the long of the specified path.
     * <p>
     * If the long could not be obtained, this method returns 0.
     *
     * @param path the path to get the long
     * @return the long
     */
    long getLong(@NotNull String path);

    /**
     * Gets the long of the specified path.
     *
     * @param path the path to get the long
     * @param def  the default long to return if the long could not be obtained
     * @return the long
     */
    long getLong(@NotNull String path, long def);

    /**
     * Gets the short of the specified path.
     * <p>
     * If the short could not be obtained, this method returns 0.
     *
     * @param path the path to get the short
     * @return the short
     */
    short getShort(@NotNull String path);

    /**
     * Gets the short of the specified path.
     *
     * @param path the path to get the short
     * @param def  the default short to return if the short could not be obtained
     * @return the short
     */
    short getShort(@NotNull String path, short def);

    /**
     * Gets the string of the specified path.
     * <p>
     * If the string could not be obtained, this method returns empty string.
     *
     * @param path the path to get the string
     * @return the string
     */
    @NotNull String getString(@NotNull String path);

    /**
     * Gets the string of the specified path.
     *
     * @param path the path to get the string
     * @param def  the default string to return if the string could not be obtained
     * @return the string
     */
    @NotNull String getString(@NotNull String path, @NotNull String def);

    /**
     * Gets the boolean list of the specified path.
     * <p>
     * If the boolean list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a boolean, it will be excluded.
     *
     * @param path the path to get the boolean list
     * @return the boolean list
     */
    @NotNull @Unmodifiable List<Boolean> getBooleanList(@NotNull String path);

    /**
     * Gets the boolean list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a boolean, it will be excluded.
     *
     * @param path the path to get the boolean list
     * @param def  the default boolean list to return if the boolean list could not be obtained
     * @return the boolean list
     */
    @NotNull @Unmodifiable List<Boolean> getBooleanList(@NotNull String path, @NotNull List<Boolean> def);

    /**
     * Gets the byte list of the specified path.
     * <p>
     * If the byte list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the byte range,
     * it will be converted to a byte using {@link Number#byteValue()}.
     *
     * @param path the path to get the byte list
     * @return the byte list
     */
    @NotNull @Unmodifiable List<Byte> getByteList(@NotNull String path);

    /**
     * Gets the byte list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the byte range,
     * it will be converted to a byte using {@link Number#byteValue()}.
     *
     * @param path the path to get the byte list
     * @param def  the default byte list to return if the byte list could not be obtained
     * @return the byte list
     */
    @NotNull @Unmodifiable List<Byte> getByteList(@NotNull String path, @NotNull List<Byte> def);

    /**
     * Gets the double list of the specified path.
     * <p>
     * If the double list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the double range,
     * it will be converted to a byte using {@link Number#doubleValue()}.
     *
     * @param path the path to get the double list
     * @return the double list
     */
    @NotNull @Unmodifiable List<Double> getDoubleList(@NotNull String path);

    /**
     * Gets the double list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the double range,
     * it will be converted to a byte using {@link Number#doubleValue()}.
     *
     * @param path the path to get the double list
     * @param def  the default double list to return if the double list could not be obtained
     * @return the double list
     */
    @NotNull @Unmodifiable List<Double> getDoubleList(@NotNull String path, @NotNull List<Double> def);

    /**
     * Gets the float list of the specified path.
     * <p>
     * If the float list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the float range,
     * it will be converted to a byte using {@link Number#floatValue()}.
     *
     * @param path the path to get the float list
     * @return the float list
     */
    @NotNull @Unmodifiable List<Float> getFloatList(@NotNull String path);

    /**
     * Gets the float list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the float range,
     * it will be converted to a byte using {@link Number#floatValue()}.
     *
     * @param path the path to get the float list
     * @param def  the default float list to return if the float list could not be obtained
     * @return the float list
     */
    @NotNull @Unmodifiable List<Float> getFloatList(@NotNull String path, @NotNull List<Float> def);

    /**
     * Gets the integer list of the specified path.
     * <p>
     * If the integer list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the int range,
     * it will be converted to a byte using {@link Number#intValue()}.
     *
     * @param path the path to get the integer list
     * @return the integer list
     */
    @NotNull @Unmodifiable List<Integer> getIntegerList(@NotNull String path);

    /**
     * Gets the integer list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the int range,
     * it will be converted to a byte using {@link Number#intValue()}.
     *
     * @param path the path to get the integer list
     * @param def  the default integer list to return if the integer list could not be obtained
     * @return the integer list
     */
    @NotNull @Unmodifiable List<Integer> getIntegerList(@NotNull String path, @NotNull List<Integer> def);

    /**
     * Gets the long list of the specified path.
     * <p>
     * If the long list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the long range,
     * it will be converted to a byte using {@link Number#longValue()}.
     *
     * @param path the path to get the long list
     * @return the long list
     */
    @NotNull @Unmodifiable List<Long> getLongList(@NotNull String path);

    /**
     * Gets the long list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the long range,
     * it will be converted to a byte using {@link Number#longValue()}.
     *
     * @param path the path to get the long list
     * @param def  the default long list to return if the long list could not be obtained
     * @return the long list
     */
    @NotNull @Unmodifiable List<Long> getLongList(@NotNull String path, @NotNull List<Long> def);

    /**
     * Gets the short list of the specified path.
     * <p>
     * If the short list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the short range,
     * it will be converted to a byte using {@link Number#shortValue()}.
     *
     * @param path the path to get the short list
     * @return the short list
     */
    @NotNull @Unmodifiable List<Short> getShortList(@NotNull String path);

    /**
     * Gets the short list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)}
     * contains an object that cannot be converted to a number, it will be excluded.
     * <p>
     * If the list contains a number that is outside the short range,
     * it will be converted to a byte using {@link Number#shortValue()}.
     *
     * @param path the path to get the short list
     * @param def  the default short list to return if the short list could not be obtained
     * @return the short list
     */
    @NotNull @Unmodifiable List<Short> getShortList(@NotNull String path, @NotNull List<Short> def);

    /**
     * Gets the string list of the specified path.
     * <p>
     * If the string list could not be obtained,
     * this method returns {@link Collections#emptyList()}.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)} contains non-string object,
     * it will be converted to a string based on {@link Object#toString()}
     * or the <code>toString</code> implementation of that object.
     *
     * @param path the path to get the string list
     * @return the string list
     */
    @NotNull @Unmodifiable List<String> getStringList(@NotNull String path);

    /**
     * Gets the string list of the specified path.
     * <p>
     * If the list that obtained from {@link Configuration#getList(String)} contains non-string object,
     * it will be converted to a string based on {@link Object#toString()}
     * or the <code>toString</code> implementation of that object.
     *
     * @param path the path to get the string list
     * @param def  the default string list to return if the string list could not be obtained
     * @return the string list
     */
    @NotNull @Unmodifiable List<String> getStringList(@NotNull String path, @NotNull List<String> def);

    /**
     * Gets the byte array.
     * <p>
     * This method decodes a base64 string into a byte array using {@link java.util.Base64#getDecoder()}.
     *
     * @param path the path to get the byte array
     * @return the decoded byte array or the empty byte array if could not decode base64 string
     */
    byte[] getBytes(@NotNull String path);

    /**
     * Sets the byte array.
     * <p>
     * This method encodes a byte array into a base64 string using {@link java.util.Base64#getEncoder}.
     *
     * @param path  the path to set the byte array
     * @param bytes the byte array to set
     */
    void setBytes(@NotNull String path, byte[] bytes);
}
