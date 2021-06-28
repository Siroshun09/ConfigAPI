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

package com.github.siroshun09.configapi.api.test.file;

import com.github.siroshun09.configapi.api.file.PropertiesFileConfiguration;
import com.github.siroshun09.configapi.api.util.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class PropertiesFileConfigurationTest {

    private static final Path PROPERTIES_PATH = Path.of("test.properties");

    @Test
    void testSaving() throws IOException {
        var config = PropertiesFileConfiguration.create(PROPERTIES_PATH);

        config.set("aaa.bbb.ccc", "value");
        config.set("example.key", 1);

        config.save();

        var sameConfig = PropertiesFileConfiguration.create(PROPERTIES_PATH);
        sameConfig.load();

        Assertions.assertEquals(config, sameConfig);

        Files.delete(PROPERTIES_PATH);
    }

    @Test @Disabled("Because it fails only on GitHub Actions and succeeds on Windows / Linux (Ubuntu).")
    void testLoading() throws IOException {
        if (Files.exists(PROPERTIES_PATH)) {
            Files.delete(PROPERTIES_PATH);
        }

        ResourceUtils.copyFromClassLoaderIfNotExists(getClass().getClassLoader(), "test.properties", PROPERTIES_PATH);

        var config = PropertiesFileConfiguration.create(PROPERTIES_PATH);

        config.load();

        Assertions.assertEquals("value", config.get("example.key"));
        Assertions.assertEquals("number", config.get("1"));

        Files.delete(PROPERTIES_PATH);
    }
}
