package com.github.siroshun09.configapi.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An interface that gets the value by path.
 * <p>
 * Using null for @NotNull argument will cause a {@link NullPointerException}.
 */
public interface Configuration {

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
    default int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    /**
     * Gets the requested integer by path.
     *
     * @param path Path of the integer to get.
     * @param def  The default value to return if the value could not be obtained.
     * @return Requested integer.
     */
    int getInt(@NotNull String path, int def);

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
}
