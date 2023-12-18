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

package com.github.siroshun09.configapi.core.node.visitor;

import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringNodeVisitorTest {

    private static final String EXPECTED_STRING_RESULT =
            "{string=value," +
                    "integer=100," +
                    "double=3.14," +
                    "bool=true," +
                    "list=[A,B,C]," +
                    "map={key=value}," +
                    "nested={map={key=value}}," +
                    "array=[1, 2, 3]," +
                    "commented=commented," +
                    "\"test's \\\"key\\\"\"=\"test's \\\"value\\\"\"" +
                    "}";

    @Test
    void testSample() {
        var visitor = StringNodeVisitor.create();
        var node = Samples.mapNode();

        node.set("array", new int[]{1, 2, 3});
        node.set("commented", CommentableNode.withComment(StringValue.fromString("commented"), Samples.comment()));
        node.set("test's \"key\"", "test's \"value\"");

        node.accept(visitor);

        Assertions.assertEquals(EXPECTED_STRING_RESULT, visitor.toString());
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
