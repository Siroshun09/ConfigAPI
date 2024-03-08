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

package com.github.siroshun09.configapi.core.node.visitor;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CharArray;
import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StringNodeVisitorTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void testResult(TestCase testCase) {
        var visitor = StringNodeVisitor.create();
        Assertions.assertSame(VisitResult.CONTINUE, testCase.node().accept(visitor));
        Assertions.assertEquals(testCase.expectedResult(), visitor.toString());
    }

    private static Stream<TestCase> testCases() {
        return Stream.of(
                testCase(new StringValue("test"), "test"),
                testCase(new StringValue(""), "\"\""),
                testCase(new StringValue("\"test\""), "\"\\\"test\\\"\""),
                testCase(new StringValue("te st"), "\"te st\""),
                testCase(new StringValue("\\test"), "\\\\test"),
                testCase(new StringValue("test's"), "\"test's\""),
                testCase(new BooleanArray(new boolean[0]), "[]"),
                testCase(new BooleanArray(new boolean[]{true, false, true}), "[true,false,true]"),
                testCase(BooleanValue.TRUE, "true"),
                testCase(BooleanValue.FALSE, "false"),
                testCase(new ByteArray(new byte[0]), "[]"),
                testCase(new ByteArray(new byte[]{(byte) -1, (byte) 0, (byte) 1}), "[-1,0,1]"),
                testCase(new ByteValue((byte) -1), "-1"),
                testCase(new ByteValue((byte) 0), "0"),
                testCase(new ByteValue((byte) 1), "1"),
                testCase(new CharArray(new char[0]), "[]"),
                testCase(new CharArray(new char[]{'a', 'b', 'c'}), "[a,b,c]"),
                testCase(new CharValue('a'), "a"),
                testCase(new DoubleArray(new double[0]), "[]"),
                testCase(new DoubleArray(new double[]{-1.5, 0.0, 1.5}), "[-1.5,0.0,1.5]"),
                testCase(new DoubleValue(-1.5), "-1.5"),
                testCase(new DoubleValue(0.0), "0.0"),
                testCase(new DoubleValue(1.5), "1.5"),
                testCase(new FloatArray(new float[0]), "[]"),
                testCase(new FloatArray(new float[]{-1.5f, 0.0f, 1.5f}), "[-1.5,0.0,1.5]"),
                testCase(new FloatValue(-1.5f), "-1.5"),
                testCase(new FloatValue(0.0f), "0.0"),
                testCase(new FloatValue(1.5f), "1.5"),
                testCase(new IntArray(new int[0]), "[]"),
                testCase(new IntArray(new int[]{-1, 0, 1}), "[-1,0,1]"),
                testCase(new IntValue(-1), "-1"),
                testCase(new IntValue(0), "0"),
                testCase(new IntValue(1), "1"),
                testCase(new LongArray(new long[0]), "[]"),
                testCase(new LongArray(new long[]{-1, 0, 1}), "[-1,0,1]"),
                testCase(new LongValue(-1), "-1"),
                testCase(new LongValue(0), "0"),
                testCase(new LongValue(1), "1"),
                testCase(new ShortArray(new short[0]), "[]"),
                testCase(new ShortArray(new short[]{(short) -1, (short) 0, (short) 1}), "[-1,0,1]"),
                testCase(new ShortValue((short) -1), "-1"),
                testCase(new ShortValue((short) 0), "0"),
                testCase(new ShortValue((short) 1), "1"),
                testCase(new EnumValue<>(ExampleEnum.B), "B"),
                testCase(NullNode.NULL, "null"),
                testCase(ListNode.create(List.of("a", "b", "c")), "[a,b,c]"),
                // Create a LinkedHashMap to avoid order issue
                testCase(MapNode.create(Stream.of(1, 2).collect(Collectors.toMap(num -> "key_" + num, num -> "value_" + num, (key1, key2) -> key2, LinkedHashMap::new))), "{key_1=value_1,key_2=value_2}")
        ).flatMap(testCase -> Stream.of(
                testCase,
                // The node is in List, and Map (as both key and value)
                testCase(ListNode.create(List.of(testCase.node())), "[" + testCase.expectedResult() + "]"),
                testCase(MapNode.create(Map.of(testCase.node(), "value")), "{" + testCase.expectedResult() + "=value}"),
                testCase(MapNode.create(Map.of("key", testCase.node())), "{key=" + testCase.expectedResult() + "}")
        )).flatMap(testCase -> Stream.of(
                testCase,
                // Even if the node is commented, the visitor should create the same result.
                testCase(CommentableNode.withComment(testCase.node(), COMMENT), testCase.expectedResult())
        ));
    }

    private static TestCase testCase(Node<?> node, String expectedResult) {
        return new TestCase(node, expectedResult);
    }

    private static final SimpleComment COMMENT = SimpleComment.create("test");

    private record TestCase(Node<?> node, String expectedResult) {
    }

    private enum ExampleEnum {
        A, B, C
    }

    @Test
    void testEscapeString() {
        Assertions.assertEquals("\"\"", quoteAndEscape(""));
        Assertions.assertEquals("test", quoteAndEscape("test"));
        Assertions.assertEquals("\"test's\"", quoteAndEscape("test's"));
        Assertions.assertEquals("\"test s\"", quoteAndEscape("test s"));
        Assertions.assertEquals("\"test\\\"A\\\"\"", quoteAndEscape("test\"A\""));
    }

    private static @NotNull String quoteAndEscape(@NotNull String str) {
        var builder = new StringBuilder();
        StringNodeVisitor.appendQuoteAndEscapedString(str, builder);
        return builder.toString();
    }
}
