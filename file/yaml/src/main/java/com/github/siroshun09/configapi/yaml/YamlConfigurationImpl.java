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

import com.github.siroshun09.configapi.common.AbstractConfiguration;
import com.github.siroshun09.configapi.common.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

class YamlConfigurationImpl extends AbstractConfiguration implements YamlConfiguration {

    private final Path filePath;
    private final ThreadLocal<Yaml> yamlThreadLocal;
    private boolean isLoaded;

    YamlConfigurationImpl(@NotNull Path filePath, @NotNull Supplier<Yaml> yamlSupplier) {
        this.filePath = Objects.requireNonNull(filePath);

        Objects.requireNonNull(yamlSupplier);
        this.yamlThreadLocal = ThreadLocal.withInitial(yamlSupplier);
    }

    YamlConfigurationImpl(@NotNull Path filePath, @NotNull Map<String, Object> map, @NotNull Supplier<Yaml> yamlSupplier) {
        super(map);
        this.filePath = Objects.requireNonNull(filePath);

        Objects.requireNonNull(yamlSupplier);
        this.yamlThreadLocal = ThreadLocal.withInitial(yamlSupplier);
    }

    YamlConfigurationImpl(@NotNull Path filePath, @NotNull AbstractConfiguration original, @NotNull Supplier<Yaml> yamlSupplier) {
        super(original);
        this.filePath = Objects.requireNonNull(filePath);

        Objects.requireNonNull(yamlSupplier);
        this.yamlThreadLocal = ThreadLocal.withInitial(yamlSupplier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() throws IOException {
        if (!Files.isRegularFile(filePath)) {
            return;
        }

        Yaml yaml = yamlThreadLocal.get();
        Map<String, Object> map;

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            map = yaml.loadAs(reader, LinkedHashMap.class);
        }

        getMap().clear();
        getMap().putAll(map);

        isLoaded = true;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void save() throws IOException {
        FileUtils.createFileIfNotExists(filePath);

        Yaml yaml = yamlThreadLocal.get();

        try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            yaml.dump(getMap(), writer);
        }
    }

    @Override
    public @NotNull Path getPath() {
        return filePath;
    }
}
