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

package com.github.siroshun09.configapi.core.file.java.properties;

import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import com.github.siroshun09.configapi.test.shared.util.Replacer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

class PropertiesFormatTest {

    private static final String SAMPLE_PROPERTIES = Replacer.lines("""
            a=b
            1=2\\=3
            1\\=2=3
            empty=
            =empty
            """);

    @Test
    void testLoading() throws IOException {
        var expected = MapNode.create();

        expected.set("a", "b");
        expected.set("1", "2=3");
        expected.set("1=2", "3");
        expected.set("empty", "");
        expected.set("", "empty");

        MapNode mapNode;
        try (var reader = new StringReader(SAMPLE_PROPERTIES)) {
            mapNode = PropertiesFormat.DEFAULT.load(reader);
        }

        NodeAssertion.assertEquals(expected, mapNode);
    }

    @Test
    void testSaving() throws IOException {
        var mapNode = MapNode.create();

        mapNode.set("a", "b");
        mapNode.set(1, "2=3");
        mapNode.set("1=2", 3);
        mapNode.set("empty", "");
        mapNode.set("", "empty");

        try (var writer = new StringWriter()) {
            PropertiesFormat.DEFAULT.save(mapNode, writer);
            Assertions.assertEquals(SAMPLE_PROPERTIES, Replacer.lines(writer.toString()));
        }

        mapNode.set("invalid", ListNode.create());
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertiesFormat.DEFAULT.save(mapNode, Writer.nullWriter()));
    }
}
