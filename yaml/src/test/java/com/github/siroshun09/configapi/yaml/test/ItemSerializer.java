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

package com.github.siroshun09.configapi.yaml.test;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import com.github.siroshun09.configapi.api.serializer.ConfigurationSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemSerializer implements ConfigurationSerializer<Item> {

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Configuration serialize(@NotNull Item item) {
        Configuration config = MappedConfiguration.create();

        config.set("part_no", item.getPartNo());
        config.set("descrip", item.getDescription());
        config.set("price", item.getPrice());
        config.set("size", item.getSize());
        config.set("quantity", item.getQuantity());

        return config;
    }

    @Override
    public @NotNull Item deserializeConfiguration(@NotNull Configuration source) {
        String partNo = source.getString("part_no");
        String description = source.getString("descrip");
        double price = source.getDouble("price");
        int size = source.getInteger("size");
        int quantity = source.getInteger("quantity");

        return new Item(partNo, description, price, size, quantity);
    }
}
