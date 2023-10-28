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

package com.github.siroshun09.configapi.core.serialization.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface to generated keys from strings.
 */
public interface KeyGenerator {

    /**
     * A {@link KeyGenerator} implementation that returns the given string as-is, or
     * an empty string if the argument is {@code null}.
     */
    KeyGenerator AS_IS = source -> source != null ? source : "";

    /**
     * A {@link KeyGenerator} implementation that converts camelCase to kebab-case.
     */
    KeyGenerator CAMEL_TO_KEBAB = source -> BasicKeyGenerators.convertCamel(source, '-');

    /**
     * A {@link KeyGenerator} implementation that converts camelCase to snake_case.
     */
    KeyGenerator CAMEL_TO_SNAKE = source -> BasicKeyGenerators.convertCamel(source, '_');

    /**
     * Generates the key from the given string.
     *
     * @param source the string to generate the key
     * @return the generated key
     */
    @NotNull String generate(@Nullable String source);

}
