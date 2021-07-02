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

package com.github.siroshun09.configapi.api.test.util;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceUtilsTest {

    private static final Path JAR_PATH = Path.of("test.jar");
    private static final Path TEXT_PATH = Path.of("test.txt");

    @Test
    void testCopyingFromClassLoader() throws IOException {
        if (Files.exists(JAR_PATH)) {
            Files.delete(JAR_PATH);
        }

        ResourceUtils.copyFromClassLoader(getClass().getClassLoader(), "example.jar", JAR_PATH);

        Assertions.assertTrue(Files.exists(JAR_PATH));

        Files.delete(JAR_PATH);
    }

    @Test
    void testCopyingFromJar() throws IOException {
        if (Files.exists(TEXT_PATH)) {
            Files.delete(TEXT_PATH);
        }

        ResourceUtils.copyFromClassLoaderIfNotExists(getClass().getClassLoader(), "example.jar", JAR_PATH);

        ResourceUtils.copyFromJar(JAR_PATH, "test.txt", TEXT_PATH);
        Assertions.assertTrue(Files.exists(TEXT_PATH));

        Files.delete(TEXT_PATH);
    }
}
