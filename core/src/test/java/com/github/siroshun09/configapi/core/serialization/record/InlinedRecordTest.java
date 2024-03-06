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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.annotation.Comment;
import com.github.siroshun09.configapi.core.serialization.annotation.Inline;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.siroshun09.configapi.core.serialization.record.RecordTestCase.create;

class InlinedRecordTest {

    @ParameterizedTest
    @MethodSource("testCases")
    <R extends Record> void testInlinedRecord(@NotNull RecordTestCase<R> testCase) {
        testCase.testDefaultSerializers();
        testCase.testDefaultDeserializers();
    }

    private static @NotNull Stream<RecordTestCase<?>> testCases() {
        return Stream.of(
                create(
                        new InlinedSingleValue(new SingleValue("test")),
                        mapNode -> mapNode.set("value", "test")
                ),
                create(
                        new InlinedCommentedSingleValue(new CommentedSingleValue("test")),
                        mapNode -> mapNode.set("value", CommentableNode.withComment(new StringValue("test"), SimpleComment.create("test")))
                ),
                create(
                        new InlinedMultipleValues(new MultipleValues("test", 1, true)),
                        mapNode -> {
                            mapNode.set("value", "test");
                            mapNode.set("number", 1);
                            mapNode.set("bool", CommentableNode.withComment(BooleanValue.TRUE, SimpleComment.create("magic boolean")));
                        }
                ),
                create(
                        new MultipleValuesInlinedTwice(new InlinedMultipleValues(new MultipleValues("test", 1, true))),
                        mapNode -> {
                            mapNode.set("value", "test");
                            mapNode.set("number", 1);
                            mapNode.set("bool", CommentableNode.withComment(BooleanValue.TRUE, SimpleComment.create("magic boolean")));
                        }
                )
        );
    }

    private record InlinedSingleValue(@Inline SingleValue record) {
    }

    private record SingleValue(String value) {
    }

    private record InlinedCommentedSingleValue(@Inline CommentedSingleValue record) {
    }

    private record CommentedSingleValue(@Comment("test") String value) {
    }

    private record InlinedMultipleValues(@Inline MultipleValues values) {
    }

    private record MultipleValues(String value, int number, @Comment("magic boolean") boolean bool) {
    }

    private record MultipleValuesInlinedTwice(@Inline InlinedMultipleValues values) {
    }
}
