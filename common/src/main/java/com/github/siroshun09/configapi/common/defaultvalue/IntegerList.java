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

package com.github.siroshun09.configapi.common.defaultvalue;

import com.github.siroshun09.configapi.common.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that represents the list that can be set with {@link Integer} and their keys.
 */
public class IntegerList extends AbstractDefaultValue<List<Integer>> {

    IntegerList(@NotNull String key, @NotNull List<Integer> def) {
        super(key, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<Integer> getValueOrNull(@NotNull Configuration configuration) {
        return configuration.getIntegerList(getKey(), getDefault());
    }
}