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

package com.github.siroshun09.configapi.api.value;

import com.github.siroshun09.configapi.api.Configuration;
import org.jetbrains.annotations.NotNull;

/**
 * An interface that represents the values that can be obtained from the {@link Configuration}.
 *
 * @param <T> the type of value to be obtained
 */
@FunctionalInterface
public interface ConfigValue<T> {

    /**
     * Gets the value from {@link Configuration}
     *
     * @param config the source {@link Configuration}
     * @return the value
     */
    @NotNull T get(@NotNull Configuration config);
}
