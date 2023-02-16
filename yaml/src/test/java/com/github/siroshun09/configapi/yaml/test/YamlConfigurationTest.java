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

package com.github.siroshun09.configapi.yaml.test;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigurationTest {

    private static final Path YAML_PATH = Path.of("test.yml");

    @Test
    void testSaving() throws IOException {
        var config = YamlConfiguration.create(YAML_PATH);

        config.set("aaa.bbb.ccc", "value");
        config.set("example.key", 1);
        config.set("example.list", List.of("A", "B", "C"));

        config.save();

        var sameConfig = YamlConfiguration.create(YAML_PATH);
        sameConfig.load();

        Assertions.assertEquals("value", sameConfig.get("aaa.bbb.ccc"));
        Assertions.assertEquals(1, sameConfig.get("example.key"));
        Assertions.assertEquals(List.of("A", "B", "C"), sameConfig.get("example.list"));

        Files.delete(YAML_PATH);
    }

    @Test
    void testLoading() throws IOException {
        if (Files.exists(YAML_PATH)) {
            Files.delete(YAML_PATH);
        }

        ResourceUtils.copyFromClassLoaderIfNotExists(getClass().getClassLoader(), "example.yml", YAML_PATH);

        var yaml = YamlConfiguration.create(YAML_PATH);

        yaml.load();

        assertEquals("Oz-Ware Purchase Invoice", yaml.getString("receipt"));
        assertEquals("Dorothy", yaml.getString("customer.first_name"));
        assertEquals("Gale", yaml.getString("customer.family_name"));
        assertEquals(
                "Follow the Yellow Brick Road to the Emerald City." +
                        " Pay no attention to the man behind the curtain.\n",
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

        Files.delete(YAML_PATH);
    }

    @Test
    void testCreating() throws IOException {
        Files.deleteIfExists(YAML_PATH);

        var classLoader = getClass().getClassLoader();

        ResourceUtils.copyFromClassLoader(classLoader, "example.yml", YAML_PATH);

        var fromFile = YamlConfiguration.create(YAML_PATH);
        fromFile.load();

        Configuration fromInputStream;

        try (var input = ResourceUtils.getInputStreamFromClassLoader(classLoader, "example.yml")) {
            fromInputStream = YamlConfiguration.loadFromInputStream(input);
        }

        assertEquals(fromFile.getLoadedConfiguration(), fromInputStream);
    }
}
