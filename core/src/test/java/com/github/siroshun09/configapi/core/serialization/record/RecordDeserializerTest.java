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
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class RecordDeserializerTest {

    @Test
    void testDeserialize() {
        var deserializer = RecordDeserializer.create(Samples.Record.class);
        Assertions.assertEquals(Samples.record(), deserializer.deserialize(Samples.mapNode()));
    }

    @Test
    void testDefaultValueByAnnotation() {
        var deserializer = RecordDeserializer.create(Samples.Record.class);
        var expected = Samples.record();
        var defaultRecord = deserializer.deserialize(MapNode.empty());
        Assertions.assertEquals(expected.string(), defaultRecord.string());
        Assertions.assertEquals(expected.integer(), defaultRecord.integer());
        Assertions.assertEquals(expected.doubleValue(), defaultRecord.doubleValue());
        Assertions.assertEquals(expected.bool(), defaultRecord.bool());
    }

    @Test
    void testDefaultValueByDefaultRecord() {
        var expected = Samples.record();
        var deserializer = RecordDeserializer.create(expected);
        Assertions.assertEquals(expected, deserializer.deserialize(MapNode.empty()));
    }

    @Test
    void testCustomObjectAndCustomKeyGenerator() {
        Assertions.assertThrows(SerializationException.class, () -> RecordDeserializer.create(Samples.UUIDRecord.class).deserialize(Samples.uuidRecordMapNode()));

        var deserializer = RecordDeserializer.builder(Samples.UUIDRecord.class).addDeserializer(UUID.class, node -> UUID.fromString(node.value().toString())).keyGenerator(KeyGenerator.CAMEL_TO_KEBAB).build();
        Assertions.assertEquals(Samples.uuidRecord(), deserializer.deserialize(Samples.uuidRecordMapNode()));
    }

    @Test
    void testDeserializingCommentedNode() {
        var deserializer = RecordDeserializer.create(SimpleRecord.class);

        var mapNode = MapNode.create();
        mapNode.set("value", CommentableNode.withComment(StringValue.fromString("value"), SimpleComment.create("comment")));

        var actual = deserializer.deserialize(mapNode);
        Assertions.assertEquals("value", actual.value());
    }

    private record SimpleRecord(String value) {
    }
}
