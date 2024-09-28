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

package dev.siroshun.configapi.format.gson;

import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.test.shared.file.JsonFileFormatTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

class GsonFormatTest extends JsonFileFormatTest<GsonFormat> {

    @Override
    protected Stream<GsonFormat> fileFormats() {
        return Stream.of(GsonFormat.DEFAULT, GsonFormat.PRETTY_PRINTING);
    }

    @Override
    protected @NotNull String extension() {
        return ".json";
    }

    @Override
    protected @NotNull MapNode emptyNode() {
        return MapNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return true;
    }

    @Override
    protected GsonFormat defaultPrinting() {
        return GsonFormat.DEFAULT;
    }

    @Override
    protected GsonFormat prettyPrinting() {
        return GsonFormat.PRETTY_PRINTING;
    }

    @Test
    void testNotSupportedObject() throws IOException {
        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("custom", new CustomObject());
            Assertions.assertThrows(IOException.class, () -> GsonFormat.DEFAULT.save(mapNode, writer));
        }
    }

    private static class CustomObject {
        private final int value = 100;
    }
}
