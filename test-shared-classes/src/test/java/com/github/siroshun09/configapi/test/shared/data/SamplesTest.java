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

package com.github.siroshun09.configapi.test.shared.data;

import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SamplesTest {

    @Test
    void testMapNode() {
        var mapNode = Samples.mapNode();
        Assertions.assertEquals("value", mapNode.getString("string"));
        Assertions.assertEquals(100, mapNode.getInteger("integer"));
        Assertions.assertEquals(3.14, mapNode.getDouble("double"));
        Assertions.assertTrue(mapNode.getBoolean("bool"));
        NodeAssertion.assertEquals(ListNode.create(List.of("A", "B", "C")), mapNode.getList("list"));
        NodeAssertion.assertEquals(MapNode.create(Map.of("key", "value")), mapNode.getMap("map"));
        NodeAssertion.assertEquals(MapNode.create(Map.of("map", Map.of("key", "value"))), mapNode.getMap("nested"));
    }
}
