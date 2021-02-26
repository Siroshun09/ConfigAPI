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

import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.configapi.yaml.test.impl.Item;
import com.github.siroshun09.configapi.yaml.test.impl.ItemSerializer;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class YamlConfigurationTest {

    @Test
    void loadTest() throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource("example.yml");

        assertNotNull(resourceUrl);

        Path path = Paths.get(resourceUrl.toURI());
        YamlConfiguration yaml = YamlConfiguration.create(path);

        assertDoesNotThrow(yaml::load);

        assertEquals("Oz-Ware Purchase Invoice", yaml.getString("receipt"));
        assertEquals("Dorothy", yaml.getString("customer.first_name"));
        assertEquals("Gale", yaml.getString("customer.family_name"));
        assertEquals(
                "Follow the Yellow Brick Road to the Emerald City." +
                        " Pay no attention to the man behind the curtain.",
                yaml.getString("specialDelivery")
        );

        assertEquals("123 Tornado Alley\nSuite 16\n", yaml.getString("bill-to.street"));
        assertEquals("East Centerville", yaml.getString("bill-to.city"));
        assertEquals("KS", yaml.getString("bill-to.state"));

        assertEquals("123 Tornado Alley\nSuite 16\n", yaml.getString("ship-to.street"));
        assertEquals("East Centerville", yaml.getString("ship-to.city"));
        assertEquals("KS", yaml.getString("ship-to.state"));

        List<Item> list = yaml.getList("items", new ItemSerializer());

        assertEquals(2, list.size());

        Item item1 = list.get(0);

        assertEquals("A4786", item1.getPartNo());
        assertEquals("Water Bucket (Filled)", item1.getDescription());
        assertEquals(1.47, item1.getPrice());
        assertEquals(4, item1.getQuantity());

        Item item2 = list.get(1);

        assertEquals("E1628", item2.getPartNo());
        assertEquals("High Heeled \"Ruby\" Slippers", item2.getDescription());
        assertEquals(8, item2.getSize());
        assertEquals(133.7, item2.getPrice());
        assertEquals(1, item2.getQuantity());
    }

    @Test
    void saveTest() throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource("example.yml");

        assertNotNull(resourceUrl);

        Path path = Paths.get(resourceUrl.toURI());
        YamlConfiguration config = YamlConfiguration.create(path);

        config.set("test", "abc");
        config.set("aaa.bbb.ccc", 100);
    }
}
