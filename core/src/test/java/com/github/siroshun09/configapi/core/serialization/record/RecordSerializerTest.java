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

package com.github.siroshun09.configapi.core.serialization.record;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.annotation.Comment;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class RecordSerializerTest {

    @Test
    void testSerialize() {
        var record = Samples.record();
        NodeAssertion.assertEquals(Samples.mapNode(), RecordSerializer.serializer().serialize(record));
    }

    @Test
    void testCustomObjectAndCustomKeyGenerator() {
        var record = Samples.uuidRecord();
        Assertions.assertThrows(SerializationException.class, () -> RecordSerializer.serializer().serialize(record));

        var serializer = RecordSerializer.builder().addSerializer(UUID.class, uuid -> new StringValue(uuid.toString())).keyGenerator(KeyGenerator.CAMEL_TO_KEBAB).build();
        NodeAssertion.assertEquals(Samples.uuidRecordMapNode(), serializer.serialize(Samples.uuidRecord()));
    }

    @Test
    void testComment() {
        var expected = MapNode.create();

        expected.set("str", CommentableNode.withComment(StringValue.fromString("str"), SimpleComment.create("test")));
        NodeAssertion.assertEquals(
                expected,
                RecordSerializer.serializer().serialize(new CommentedRecord("str"))
        );
    }

    @Test
    void testNestedComment() {
        var expected = MapNode.create();

        var child = MapNode.create();
        child.set("str", CommentableNode.withComment(StringValue.fromString("str"), SimpleComment.create("test")));
        child.setComment(SimpleComment.create("nested"));

        expected.set("commented", child);

        NodeAssertion.assertEquals(
                expected,
                RecordSerializer.serializer().serialize(new Nested(new CommentedRecord("str")))
        );
    }

    @Test
    void testTypeSpecifiedComment() {
        var expected = MapNode.create();

        expected.set("str", CommentableNode.withComment(StringValue.fromString("str"), SimpleComment.create("test", "block")));
        NodeAssertion.assertEquals(
                expected,
                RecordSerializer.serializer().serialize(new TypeSpecified("str"))
        );
    }

    private record CommentedRecord(@Comment("test") String str) {
    }

    private record Nested(@Comment("nested") CommentedRecord commented) {
    }

    private record TypeSpecified(@Comment(value = "test", type = "block") String str) {
    }
}
