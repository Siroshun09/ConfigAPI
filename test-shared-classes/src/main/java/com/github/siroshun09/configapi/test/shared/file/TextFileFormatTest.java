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

package com.github.siroshun09.configapi.test.shared.file;

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import com.github.siroshun09.configapi.test.shared.util.Replacer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An abstract class for testing {@link FileFormat} implementations that load/save nodes from/to text files.
 *
 * @param <N> a {@link Node} type
 * @param <F> a {@link FileFormat} type
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TextFileFormatTest<N extends Node<?>, F extends FileFormat<N>> extends BasicFileFormatTest<N, F> {

    @ParameterizedTest
    @MethodSource("testCases")
    void test(TestCase<N, F> testCase, @TempDir Path directory) throws IOException {
        if (testCase instanceof TextFileFormatTest.LoadTestCase<N, F> loadTestCase) {
            this.doLoadTest(loadTestCase, directory);
        } else if (testCase instanceof TextFileFormatTest.SaveTestCase<N, F> saveTestCase) {
            this.doSaveTest(saveTestCase, directory);
        } else if (testCase instanceof TextFileFormatTest.SaveAndLoadTestCase<N, F> saveAndLoadTestCase) {
            this.doSaveAndLoadTest(saveAndLoadTestCase, directory);
        } else {
            throw new IllegalArgumentException("Unsupported test case: " + testCase);
        }
    }

    private void doLoadTest(LoadTestCase<N, F> testCase, Path directory) throws IOException {
        if (this.isSupportedIOType(Path.class)) {
            var filepath = directory.resolve("load-from-file" + this.extension());
            Files.writeString(filepath, testCase.text());
            testCase.checkNode(testCase.fileFormat().load(filepath));
        }

        if (this.isSupportedIOType(InputStream.class)) {
            try (var input = new ByteArrayInputStream(testCase.text().getBytes(StandardCharsets.UTF_8))) {
                testCase.checkNode(testCase.fileFormat().load(input));
            }
        }

        if (this.isSupportedIOType(Reader.class)) {
            try (var reader = new StringReader(testCase.text())) {
                testCase.checkNode(testCase.fileFormat().load(reader));
            }
        }
    }

    private void doSaveTest(SaveTestCase<N, F> testCase, Path directory) throws IOException {
        if (this.isSupportedIOType(Path.class)) {
            var filepath = directory.resolve("write-to-file" + this.extension());
            testCase.fileFormat().save(testCase.node(), filepath);
            testCase.checkText(Files.readString(filepath, StandardCharsets.UTF_8));
        }

        if (this.isSupportedIOType(OutputStream.class)) {
            try (var output = new ByteArrayOutputStream()) {
                testCase.fileFormat().save(testCase.node(), output);
                testCase.checkText(output.toString(StandardCharsets.UTF_8));
            }
        }

        if (this.isSupportedIOType(Reader.class)) {
            try (var writer = new StringWriter()) {
                testCase.fileFormat().save(testCase.node(), writer);
                testCase.checkText(writer.toString());
            }
        }
    }

    private void doSaveAndLoadTest(SaveAndLoadTestCase<N, F> testCase, Path directory) throws IOException {
        if (this.isSupportedIOType(Path.class)) {
            var filepath = directory.resolve("write-to-file" + this.extension());
            testCase.fileFormat().save(testCase.node(), filepath);
            testCase.checkText(Files.readString(filepath, StandardCharsets.UTF_8));
            testCase.checkNode(testCase.fileFormat().load(filepath));
        }

        if (this.isSupportedIOType(OutputStream.class)) {
            try (var output = new ByteArrayOutputStream()) {
                testCase.fileFormat().save(testCase.node(), output);
                testCase.checkText(output.toString(StandardCharsets.UTF_8));

                if (this.isSupportedIOType(InputStream.class)) {
                    try (var input = new ByteArrayInputStream(output.toByteArray())) {
                        testCase.checkNode(testCase.fileFormat().load(input));
                    }
                }
            }
        }

        if (this.isSupportedIOType(Reader.class)) {
            try (var writer = new StringWriter()) {
                testCase.fileFormat().save(testCase.node(), writer);
                testCase.checkText(writer.toString());

                if (this.isSupportedIOType(Reader.class)) {
                    try (var reader = new StringReader(writer.toString())) {
                        testCase.checkNode(testCase.fileFormat().load(reader));
                    }
                }
            }
        }
    }

    /**
     * Creates the test cases.
     *
     * @return the test cases
     */
    protected abstract Stream<TestCase<N, F>> testCases();

    /**
     * Creates a {@link TestCaseBase}.
     *
     * @param text a text
     * @param node a node
     * @param <N>  a {@link Node} type
     * @return a created {@link TestCaseBase}
     */
    protected static <N extends Node<?>> TestCaseBase<N> testCase(String text, N node) {
        return new TestCaseBase<>(text, node);
    }

    /**
     * A record to creating {@link TestCase}.
     *
     * @param <N>  a {@link Node} type
     * @param text a text
     * @param node a node
     */
    protected record TestCaseBase<N extends Node<?>>(String text, N node) {

        /**
         * Creates {@link LoadTestCase}s from this {@link TestCaseBase}.
         *
         * @param fileFormats {@link FileFormat}s that uses for testing
         * @param <F>         a {@link FileFormat} type
         * @return created {@link LoadTestCase}s
         */
        @SafeVarargs
        public final <F extends FileFormat<N>> Stream<LoadTestCase<N, F>> loadTest(F... fileFormats) {
            return Arrays.stream(fileFormats).map(fileFormat -> new LoadTestCase<>(fileFormat, this.text, this.node));
        }

        /**
         * Creates {@link SaveTestCase}s from this {@link TestCaseBase}.
         *
         * @param fileFormats {@link FileFormat}s that uses for testing
         * @param <F>         a {@link FileFormat} type
         * @return created {@link LoadTestCase}s
         */
        @SafeVarargs
        public final <F extends FileFormat<N>> Stream<SaveTestCase<N, F>> saveTest(F... fileFormats) {
            return Arrays.stream(fileFormats).map(fileFormat -> new SaveTestCase<>(fileFormat, this.node, this.text));
        }

        /**
         * Creates {@link SaveAndLoadTestCase}s from this {@link TestCaseBase}.
         *
         * @param fileFormats {@link FileFormat}s that uses for testing
         * @param <F>         a {@link FileFormat} type
         * @return created {@link LoadTestCase}s
         */
        @SafeVarargs
        public final <F extends FileFormat<N>> Stream<SaveAndLoadTestCase<N, F>> saveAndLoadTest(F... fileFormats) {
            return Arrays.stream(fileFormats).map(fileFormat -> new SaveAndLoadTestCase<>(fileFormat, this.node, this.text));
        }
    }

    /**
     * A root interface of the test cases.
     *
     * @param <N> a {@link Node} type
     * @param <F> a {@link FileFormat} type
     */
    protected sealed interface TestCase<N extends Node<?>, F extends FileFormat<N>> permits SaveAndLoadTestCase, LoadTestCase, SaveTestCase {
    }

    /**
     * A {@link TestCase} implementation that tests loading the node from the text.
     *
     * @param fileFormat   a {@link FileFormat} to use for loading the node
     * @param text         a text to load
     * @param expectedNode an expected {@link Node}
     * @param <N>          a {@link Node} type
     * @param <F>          a {@link FileFormat} type
     */
    protected record LoadTestCase<N extends Node<?>, F extends FileFormat<N>>(F fileFormat, String text,
                                                                              N expectedNode) implements TestCase<N, F> {
        private void checkNode(N node) {
            NodeAssertion.assertEquals(this.expectedNode, node);
        }
    }

    /**
     * A {@link TestCase} implementation that tests saving the node to the text.
     *
     * @param fileFormat   a {@link FileFormat} to use for saving the node
     * @param node         a {@link Node} to save
     * @param expectedText an expected text
     * @param <N>          a {@link Node} type
     * @param <F>          a {@link FileFormat} type
     */
    protected record SaveTestCase<N extends Node<?>, F extends FileFormat<N>>(F fileFormat, N node,
                                                                              String expectedText) implements TestCase<N, F> {
        private void checkText(String text) {
            Assertions.assertEquals(Replacer.lines(this.expectedText), Replacer.lines(text));
        }
    }

    /**
     * A {@link TestCase} implementation that tests loading/saving the node from/to the text.
     *
     * @param fileFormat a {@link FileFormat} to use for loading/saving the node
     * @param node       a {@link Node} to save and expected one when loading
     * @param text       a text to load and expected on when saving
     * @param <N>        a {@link Node} type
     * @param <F>        a {@link FileFormat} type
     */
    protected record SaveAndLoadTestCase<N extends Node<?>, F extends FileFormat<N>>(F fileFormat, N node,
                                                                                     String text) implements TestCase<N, F> {
        private void checkText(String text) {
            Assertions.assertEquals(Replacer.lines(this.text), Replacer.lines(text));
        }

        private void checkNode(N node) {
            NodeAssertion.assertEquals(this.node, node);
        }
    }
}
