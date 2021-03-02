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

package com.github.siroshun09.configapi.yaml.test.impl;

import org.jetbrains.annotations.NotNull;

public class Item {

    private final String partNo;
    private final String description;
    private final double price;
    private final int size;
    private final int quantity;

    Item(@NotNull String partNo, @NotNull String description, double price, int size, int quantity) {

        this.partNo = partNo;
        this.description = description;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
    }

    public @NotNull String getPartNo() {
        return partNo;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }
}
