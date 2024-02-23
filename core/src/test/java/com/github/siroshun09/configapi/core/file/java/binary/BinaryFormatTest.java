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

package com.github.siroshun09.configapi.core.file.java.binary;

import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.CommentedNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.FloatValue;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.IntValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.LongValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class BinaryFormatTest {

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadingAndSavingFile(@NotNull Node<?> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("loading-and-saving-filepath.dat");
        Files.createFile(filepath);

        BinaryFormat.DEFAULT.save(sample, filepath);
        this.checkFileLoading(sample, filepath);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testLoadingAndSavingStream(@NotNull Node<?> sample) throws IOException {
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
    void testNonExistentFile(@TempDir Path directory) throws IOException {
        var filename = "non-existent-file.dat";
        NodeAssertion.assertEquals(NullNode.NULL, BinaryFormat.DEFAULT.load(directory.resolve(filename)));
        NodeAssertion.assertEquals(NullNode.NULL, BinaryFormat.DEFAULT.load(directory.resolve("non-existent-directory").resolve(filename)));
    }

    @ParameterizedTest
    @MethodSource("samples")
    void testSaveInNonExistentDirectory(@NotNull Node<?> sample, @TempDir Path directory) throws IOException {
        var filepath = directory.resolve("new-directory").resolve("new-file.dat");
        BinaryFormat.DEFAULT.save(sample, filepath);
        this.checkFileLoading(sample, filepath);
    }

    private static @NotNull Stream<Node<?>> samples() {
        return Stream.of(
                Samples.mapNode()
        );
    }

    private void checkFileLoading(@NotNull Node<?> sample, @NotNull Path filepath) throws IOException {
        NodeAssertion.assertEquals(sample, BinaryFormat.DEFAULT.load(filepath));

        try (var in = Files.newInputStream(filepath)) {
            NodeAssertion.assertEquals(sample, BinaryFormat.DEFAULT.load(in));
        }
    }

    static class ValueNodeTest {

        @ParameterizedTest
        @MethodSource("testCases")
        void test(TestCase<?> testCase) throws IOException {
            try (var out = new ByteArrayOutputStream()) {
                BinaryFormat.DEFAULT.save(testCase.node, out);

                try (var in = new ByteArrayInputStream(out.toByteArray())) {
                    Node<?> expected = testCase.node instanceof CommentedNode<?> commentedNode ? commentedNode.node() : testCase.node;
                    NodeAssertion.assertEquals(expected, BinaryFormat.DEFAULT.load(in));
                }
            }
        }

        @SuppressWarnings("unchecked")
        private static Stream<TestCase<?>> testCases() {
            return Stream.of(
                            Stream.of(NullNode.NULL),
                            Stream.of(BooleanValue.TRUE, BooleanValue.FALSE),
                            IntStream.of(Byte.MIN_VALUE, 0, Byte.MAX_VALUE).mapToObj(value -> new ByteValue((byte) value)),
                            DoubleStream.of(Double.MIN_VALUE, 0, Double.MAX_VALUE).mapToObj(DoubleValue::new),
                            DoubleStream.of(Float.MIN_VALUE, 0, Float.MAX_VALUE).mapToObj(value -> new FloatValue((float) value)),
                            IntStream.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE).mapToObj(IntValue::new),
                            LongStream.of(Long.MIN_VALUE, 0, Long.MAX_VALUE).mapToObj(LongValue::new),
                            IntStream.of(Short.MIN_VALUE, 0, Short.MAX_VALUE).mapToObj(value -> new ShortValue((short) value)),
                            Stream.of("a", "", "あ", "a\na").map(StringValue::fromString)
                    )
                    .flatMap(Function.identity())
                    .map(Node.class::cast) // I don't know why, but I need this to compile.
                    .flatMap(node -> Stream.of(node, CommentableNode.withComment(node, null))) // Also, tests nodes wrapped by CommentedNode
                    .map(TestCase::new);
        }

        private record TestCase<N extends Node<?>>(N node) {
            @Override
            public String toString() {
                return node.toString();
            }
        }
    }

    static class LengthTest {

        @ParameterizedTest
        @MethodSource("testCases")
        void test(TestCase<?> testCase) throws IOException {
            try (var out = new ByteArrayOutputStream()) {
                BinaryFormat.DEFAULT.save(testCase.node, out);

                try (var in = new ByteArrayInputStream(out.toByteArray())) {
                    Node<?> expected = testCase.node instanceof CommentedNode<?> commentedNode ? commentedNode.node() : testCase.node;
                    NodeAssertion.assertEquals(expected, BinaryFormat.DEFAULT.load(in));
                }
            }
        }

        private static Stream<TestCase<?>> testCases() {
            return Stream.of(
                    new TestCaseBase<>("ListNode", length -> {
                        var node = ListNode.create(length);

                        for (int i = 0; i < length; i++) {
                            node.add(BooleanValue.TRUE);
                        }

                        return node;
                    }),
                    new TestCaseBase<>("BooleanArray", length -> new BooleanArray(new boolean[length])),
                    new TestCaseBase<>("ByteArray", length -> new ByteArray(new byte[length])),
                    new TestCaseBase<>("DoubleArray", length -> new DoubleArray(new double[length])),
                    new TestCaseBase<>("FloatArray", length -> new FloatArray(new float[length])),
                    new TestCaseBase<>("IntArray", length -> new IntArray(new int[length])),
                    new TestCaseBase<>("LongArray", length -> new LongArray(new long[length])),
                    new TestCaseBase<>("ShortArray", length -> new ShortArray(new short[length])),
                    new TestCaseBase<>("ListNode (String)", length -> { // Special case: String only
                        var node = ListNode.create(length);

                        for (int i = 0; i < length; i++) {
                            node.add(String.valueOf(i));
                        }

                        return node;
                    }),
                    new TestCaseBase<>("MapNode", length -> {
                        var mapNode = MapNode.create();

                        for (int i = 0; i < length; i++) {
                            mapNode.set(String.valueOf(i), new IntValue(i));
                        }

                        return mapNode;
                    })
            ).flatMap(base -> IntStream.of( // list of length
                    0, // length data: 000
                    1, // length data: 001
                    2, // length data: 010
                    3, // length data: 011
                    4, // length data: 100
                    5, // length data: 101 + byte data
                    255, // length data: 101 + byte data
                    256, // length data: 110 + short data
                    65535, // length data: 110 + short data
                    65536 // length data: 111 + int data
            ).mapToObj(base::create)).flatMap(testCase -> {  // Also, tests nodes wrapped by CommentedNode
                if (testCase.node instanceof CommentableNode) { // ListNode and MapNode cannot be wrapped
                    return Stream.of(testCase);
                } else {
                    return Stream.of(testCase, new TestCase<>(testCase.testName + " (commented)", CommentableNode.withComment(testCase.node, null)));
                }
            });
        }

        private record TestCaseBase<N extends Node<?>>(String name, IntFunction<N> function) {
            private TestCase<N> create(int length) {
                return new TestCase<>(this.name + " (length: " + length + ")", this.function.apply(length));
            }
        }

        private record TestCase<N extends Node<?>>(String testName, N node) {
            @Override
            public String toString() {
                return this.testName;
            }
        }
    }
}
