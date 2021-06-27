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

package com.github.siroshun09.configapi.api.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceUtils {

    public static void copyFromClassLoader(@NotNull ClassLoader loader,
                                           @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(loader);
        Objects.requireNonNull(name);
        Objects.requireNonNull(target);

        try (InputStream input = loader.getResourceAsStream(name)) {
            if (input != null) {
                Files.copy(input, target);
            }
        }
    }

    public static void copyFromClassLoaderIfNotExists(@NotNull ClassLoader loader,
                                                      @NotNull String name, @NotNull Path target) throws IOException {
        Objects.requireNonNull(target);

        if (!Files.exists(target)) {
            copyFromClassLoader(loader, name, target);
        }
    }
}
