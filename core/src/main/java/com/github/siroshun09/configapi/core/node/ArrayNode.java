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

package com.github.siroshun09.configapi.core.node;

/**
 * An interface to represents that {@link Node} holds an array.
 * <p>
 * Due to the Java specification, the array held by the implementation class (record) of this interface is modifiable.
 *
 * @param <T> the type of the array
 */
public sealed interface ArrayNode<T> extends Node<T> permits BooleanArray, ByteArray, DoubleArray, FloatArray, IntArray, LongArray, ShortArray {
}
