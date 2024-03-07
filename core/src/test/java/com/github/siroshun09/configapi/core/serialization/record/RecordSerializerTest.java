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

import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.core.serialization.SerializationException;
import com.github.siroshun09.configapi.core.serialization.key.KeyGenerator;
import com.github.siroshun09.configapi.test.shared.data.Samples;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class RecordSerializerTest {

    @Test
    void testCustomObjectAndCustomKeyGenerator() {
        var record = Samples.uuidRecord();
        Assertions.assertThrows(SerializationException.class, () -> RecordSerializer.serializer().serialize(record));

        var serializer = RecordSerializer.builder().addSerializer(UUID.class, uuid -> new StringValue(uuid.toString())).keyGenerator(KeyGenerator.CAMEL_TO_KEBAB).build();
        NodeAssertion.assertEquals(Samples.uuidRecordMapNode(), serializer.serialize(Samples.uuidRecord()));
    }
}
