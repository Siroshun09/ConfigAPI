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

package dev.siroshun.configapi.core.serialization.record;

import dev.siroshun.configapi.core.comment.SimpleComment;
import dev.siroshun.configapi.core.node.CommentableNode;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.configapi.core.serialization.annotation.Comment;
import dev.siroshun.configapi.core.serialization.annotation.DefaultString;
import dev.siroshun.configapi.core.serialization.annotation.Inline;
import dev.siroshun.configapi.test.shared.util.NodeAssertion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.siroshun.configapi.core.serialization.record.RecordTestCase.create;

class CommentTest {

    @ParameterizedTest
    @MethodSource("commentedComponents")
    <R extends Record> void testCommentedComponents(@NotNull RecordTestCase<R> testCase) {
        testCase.testDefaultSerializers();
        testCase.testDefaultDeserializers();
    }

    private static @NotNull Stream<RecordTestCase<?>> commentedComponents() {
        return Stream.of(
                create(
                        new Commented("value"),
                        mapNode -> mapNode.set("str", CommentableNode.withComment(StringValue.fromString("value"), SimpleComment.create("test")))
                ),
                create(
                        new TypeSpecified("value"),
                        mapNode -> mapNode.set("str", CommentableNode.withComment(StringValue.fromString("value"), SimpleComment.create("test", "block")))
                ),
                create(
                        new Nested(new Commented("value")),
                        mapNode -> {
                            var nested = mapNode.createMap("commented");
                            nested.set("str", CommentableNode.withComment(StringValue.fromString("value"), SimpleComment.create("test")));
                            nested.setComment(SimpleComment.create("nested", "block"));
                        }
                ),
                create(
                        new Inlined(new StringPair("key", "value")),
                        mapNode -> {
                            mapNode.set("left", CommentableNode.withComment(StringValue.fromString("key"), SimpleComment.create("left")));
                            mapNode.set("right", CommentableNode.withComment(StringValue.fromString("value"), SimpleComment.create("right")));
                        }
                )
        );
    }

    @ParameterizedTest
    @MethodSource("defaultValueOfCommentedComponents")
    <R extends Record> void testDefaultValueOfCommentedComponents(@NotNull RecordTestCase<R> testCase) {
        var expectedMapNode = testCase.expectedMapNode();
        NodeAssertion.assertEquals(expectedMapNode, RecordSerializer.serializer().serializeDefault(testCase.expectedRecord().getClass()));

        var expectedRecord = testCase.expectedRecord();
        Assertions.assertEquals(expectedRecord, RecordDeserializer.create(expectedRecord.getClass()).deserialize(expectedMapNode));
        Assertions.assertEquals(expectedRecord, RecordDeserializer.create(expectedRecord).deserialize(expectedMapNode));
    }

    static @NotNull Stream<RecordTestCase<?>> defaultValueOfCommentedComponents() {
        return Stream.of(
                create(
                        new Commented("default"),
                        mapNode -> mapNode.set("str", CommentableNode.withComment(StringValue.fromString("default"), SimpleComment.create("test")))
                ),
                create(
                        new TypeSpecified("default"),
                        mapNode -> mapNode.set("str", CommentableNode.withComment(StringValue.fromString("default"), SimpleComment.create("test", "block")))
                ),
                create(
                        new Nested(new Commented("default")),
                        mapNode -> {
                            var nested = mapNode.createMap("commented");
                            nested.set("str", CommentableNode.withComment(StringValue.fromString("default"), SimpleComment.create("test")));
                            nested.setComment(SimpleComment.create("nested", "block"));
                        }
                ),
                create(
                        new Inlined(new StringPair("left", "right")),
                        mapNode -> {
                            mapNode.set("left", CommentableNode.withComment(StringValue.fromString("left"), SimpleComment.create("left")));
                            mapNode.set("right", CommentableNode.withComment(StringValue.fromString("right"), SimpleComment.create("right")));
                        }
                )
        );
    }

    private record Commented(@Comment("test") @DefaultString("default") String str) {
    }

    private record TypeSpecified(@Comment(value = "test", type = "block") @DefaultString("default") String str) {
    }

    private record Nested(@Comment(value = "nested", type = "block") Commented commented) {
    }

    private record StringPair(@Comment("left") @DefaultString("left") String left,
                              @Comment("right") @DefaultString("right") String right) {
    }

    private record Inlined(@Inline StringPair pair) {
    }
}
