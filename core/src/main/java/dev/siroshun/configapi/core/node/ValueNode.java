/*
 *     Copyright 2025 Siroshun09
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

package dev.siroshun.configapi.core.node;

import org.jetbrains.annotations.NotNullByDefault;

/**
 * An interface to represents that {@link Node} holds a value or value-based object.
 * <p>
 * The implementations of this interface should always be immutable and non-null.
 *
 * @param <T> a type of value
 */
@NotNullByDefault
public sealed interface ValueNode<T> extends Node<T> permits BooleanValue, CharValue, EnumValue, NumberValue, StringValue {

    @Override
    T value();

}
