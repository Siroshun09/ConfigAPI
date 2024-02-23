/*
 *     Copyright 2024 Siroshun09
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

package com.github.siroshun09.configapi.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.siroshun09.configapi.core.util.ResourceUtils.copyFromClassLoader;
import static com.github.siroshun09.configapi.core.util.ResourceUtils.copyFromClassLoaderIfNotExists;
import static com.github.siroshun09.configapi.core.util.ResourceUtils.copyFromJar;
import static com.github.siroshun09.configapi.core.util.ResourceUtils.copyFromJarIfNotExists;

class ResourceUtilsTest {

    @Test
    void testResourceCopy(@TempDir Path dir) throws IOException {
        var jarPath = dir.resolve("test.jar");
        var txtPath = dir.resolve("test.txt");

        copyFromClassLoader(getClass().getClassLoader(), "example.jar.dat", jarPath);
        Assertions.assertTrue(Files.exists(jarPath));

        copyFromJar(jarPath, "test.txt", txtPath);
        Assertions.assertTrue(Files.exists(txtPath));

        Assertions.assertThrows(FileAlreadyExistsException.class, () -> copyFromClassLoader(getClass().getClassLoader(), "example.jar.dat", jarPath));
        Assertions.assertDoesNotThrow(() -> copyFromClassLoaderIfNotExists(getClass().getClassLoader(), "example.jar.dat", jarPath));
        Assertions.assertThrows(FileAlreadyExistsException.class, () -> copyFromJar(jarPath, "test.txt", txtPath));
        Assertions.assertDoesNotThrow(() -> copyFromJarIfNotExists(jarPath, "test.txt", txtPath));
    }
}
