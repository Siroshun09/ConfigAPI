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

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.ObjectNode;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.test.shared.file.BasicFileFormatTest;
import com.github.siroshun09.configapi.test.shared.util.Replacer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

class PropertiesFormatTest extends BasicFileFormatTest<MapNode, PropertiesFormat> {

    private static final String SAMPLE_PROPERTIES = Replacer.lines("""
            a=b
            1=2\\=3
            1\\=2=3
            empty=
            =empty
            """);

    private static @NotNull MapNode sampleMapNode() {
        var mapNode = MapNode.create();
        mapNode.set("a", "b");
        mapNode.set("1", "2=3");
        mapNode.set("1=2", "3");
        mapNode.set("empty", "");
        mapNode.set("", "empty");
        return mapNode;
    }

    @Override
    protected @NotNull Stream<Sample<MapNode, PropertiesFormat>> samples() {
        return Stream.of(
                new Sample<>(PropertiesFormat.DEFAULT, sampleMapNode(), SAMPLE_PROPERTIES)
        );
    }

    @Override
    protected @NotNull String extension() {
        return ".properties";
    }

    @Override
    protected @NotNull MapNode emptyNode() {
        return MapNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return true;
    }

    @Test
    void testNonStringKeyAndValue() throws IOException {
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
    }

    @ParameterizedTest
    @MethodSource("nonStringRepresentableNodes")
    void testNonStringRepresentableNode(@NotNull Node<?> node) {
        var mapNode = MapNode.create();

        mapNode.set("invalid", node);
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertiesFormat.DEFAULT.save(mapNode, Writer.nullWriter()));

        mapNode.set("invalid", CommentableNode.withComment(node, SimpleComment.create("test")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertiesFormat.DEFAULT.save(mapNode, Writer.nullWriter()));
    }

    private @NotNull Stream<Node<?>> nonStringRepresentableNodes() {
        return Stream.of(
                ListNode.create(), MapNode.create(), new ObjectNode<>(new Object()),
                new BooleanArray(new boolean[0]), new ByteArray(new byte[0]), new DoubleArray(new double[0]),
                new FloatArray(new float[0]), new IntArray(new int[0]), new LongArray(new long[0]), new ShortArray(new short[0])
        );
    }
}
