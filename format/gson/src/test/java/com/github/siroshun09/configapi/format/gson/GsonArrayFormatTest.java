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

package com.github.siroshun09.configapi.format.gson;

import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.test.shared.file.TextFileFormatTest;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class GsonArrayFormatTest extends TextFileFormatTest<ListNode, GsonArrayFormat> {

    private static final String NUMBER_LIST = "[1,2,3.14]";
    private static final String NUMBER_LIST_PRETTY_PRINTING = """
            [
              1,
              2,
              3.14
            ]""";
    private static final String STRING_LIST = "[\"a\",\"b\",\"c\"]";
    private static final String STRING_LIST_PRETTY_PRINTING = """
            [
              "a",
              "b",
              "c"
            ]""";
    private static final String OBJECT_LIST = "[{\"num\":1},{\"num\":2},{\"num\":3}]";
    private static final String OBJECT_LIST_PRETTY_PRINTING = """
            [
              {
                "num": 1
              },
              {
                "num": 2
              },
              {
                "num": 3
              }
            ]""";

    private static ListNode numberListNode() {
        return ListNode.create(List.of(1, 2, 3.14));
    }

    private static ListNode stringListNode() {
        return ListNode.create(List.of("a", "b", "c"));
    }

    private static ListNode objectListNode() {
        return ListNode.create(IntStream.rangeClosed(1, 3).mapToObj(num -> Map.of("num", num)).map(MapNode::create).toList());
    }

    @Override
    protected Stream<TestCase<ListNode, GsonArrayFormat>> testCases() {
        return Stream.of(
                testCase(NUMBER_LIST, numberListNode()).saveAndLoadTest(GsonArrayFormat.DEFAULT),
                testCase(NUMBER_LIST_PRETTY_PRINTING, numberListNode()).saveAndLoadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(NUMBER_LIST, numberListNode()).loadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(NUMBER_LIST_PRETTY_PRINTING, numberListNode()).loadTest(GsonArrayFormat.DEFAULT),
                testCase(STRING_LIST, stringListNode()).saveAndLoadTest(GsonArrayFormat.DEFAULT),
                testCase(STRING_LIST_PRETTY_PRINTING, stringListNode()).saveAndLoadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(STRING_LIST, stringListNode()).loadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(STRING_LIST_PRETTY_PRINTING, stringListNode()).loadTest(GsonArrayFormat.DEFAULT),
                testCase(OBJECT_LIST, objectListNode()).saveAndLoadTest(GsonArrayFormat.DEFAULT),
                testCase(OBJECT_LIST_PRETTY_PRINTING, objectListNode()).saveAndLoadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(OBJECT_LIST, objectListNode()).loadTest(GsonArrayFormat.PRETTY_PRINTING),
                testCase(OBJECT_LIST_PRETTY_PRINTING, objectListNode()).loadTest(GsonArrayFormat.DEFAULT)
        ).flatMap(Function.identity());
    }

    @Override
    protected Stream<GsonArrayFormat> fileFormats() {
        return Stream.of(GsonArrayFormat.DEFAULT, GsonArrayFormat.PRETTY_PRINTING);
    }

    @Override
    protected @NotNull String extension() {
        return ".json";
    }

    @Override
    protected @NotNull ListNode emptyNode() {
        return ListNode.empty();
    }

    @Override
    protected boolean supportEmptyFile() {
        return true;
    }
}
