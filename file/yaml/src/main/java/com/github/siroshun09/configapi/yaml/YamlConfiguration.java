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

package com.github.siroshun09.configapi.yaml;

import com.github.siroshun09.configapi.common.Configuration;
import com.github.siroshun09.configapi.common.ConfigurationImpl;
import com.github.siroshun09.configapi.common.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public interface YamlConfiguration extends FileConfiguration {

    @Contract("_ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath) {
        return create(filePath, Yaml::new);
    }

    @Contract("_, _ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath, @NotNull Supplier<Yaml> yamlSupplier) {
        return new YamlConfigurationImpl(filePath, yamlSupplier);
    }

    @Contract("_, _ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath, @NotNull Map<String, Object> map) {
        return create(filePath, map, Yaml::new);
    }

    @Contract("_, _, _ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath, @NotNull Map<String, Object> map, @NotNull Supplier<Yaml> yamlSupplier) {
        return new YamlConfigurationImpl(filePath, map, yamlSupplier);
    }

    @Contract("_, _ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath, @NotNull Configuration original) throws UnsupportedOperationException {
        return create(filePath, original, Yaml::new);
    }

    @Contract("_, _, _ -> new")
    static @NotNull YamlConfiguration create(@NotNull Path filePath, @NotNull Configuration original,
                                             @NotNull Supplier<Yaml> yamlSupplier) throws UnsupportedOperationException {
        if (original instanceof ConfigurationImpl) {
            return new YamlConfigurationImpl(filePath, (ConfigurationImpl) original, yamlSupplier);
        } else {
            throw new UnsupportedOperationException("original configuration should be extended ConfigurationImpl");
        }
    }
}
