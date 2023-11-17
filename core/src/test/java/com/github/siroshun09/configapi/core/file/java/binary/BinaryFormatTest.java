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

package com.github.siroshun09.configapi.core.file.java.binary;

import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

class BinaryFormatTest {

    @Test
    void testSaveAndLoad() throws IOException {
        Node<?> sample = Samples.mapNode();
        byte[] bytes;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryFormat.DEFAULT.save(sample, out);
            bytes = out.toByteArray();
        }

        Node<?> loaded;

        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            loaded = BinaryFormat.DEFAULT.load(in);
        }

        NodeAssertion.assertEquals(sample, loaded);
    }

    @Test
    void testFilepath(@TempDir Path directory) throws IOException {
        Path filepath = directory.resolve("node.dat");
        Node<?> sample = Samples.mapNode();

        BinaryFormat.DEFAULT.save(sample, filepath);

        Node<?> loaded = BinaryFormat.DEFAULT.load(filepath);

        NodeAssertion.assertEquals(sample, loaded);
    }
}
