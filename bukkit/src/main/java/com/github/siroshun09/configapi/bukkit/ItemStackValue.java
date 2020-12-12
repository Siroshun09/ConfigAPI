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

package com.github.siroshun09.configapi.bukkit;

import com.github.siroshun09.configapi.common.Configuration;
import com.github.siroshun09.configapi.common.configurable.AbstractConfigurableValue;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class that represents the value that can be set with {@link ItemStack} and their keys.
 */
public class ItemStackValue extends AbstractConfigurableValue<ItemStack> {

    /**
     * Creates {@link ItemStackValue}.
     *
     * @param path The path
     * @param def  The default item
     * @return {@link ItemStackValue}
     */
    public static @NotNull ItemStackValue create(@NotNull String path, @NotNull ItemStack def) {
        return new ItemStackValue(path, def);
    }

    private ItemStackValue(@NotNull String key, @NotNull ItemStack def) {
        super(key, def);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ItemStack getValueOrNull(@NotNull Configuration configuration) {
        if (configuration instanceof BukkitYaml) {
            return ((BukkitYaml) configuration).getItemStack(getKey(), getDefault());
        } else {
            return null;
        }
    }
}
