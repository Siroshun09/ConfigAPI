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

package dev.siroshun.configapi.core.node.visitor;

import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;

/**
 * Results of visiting a {@link Node}.
 */
public enum VisitResult {

    /**
     * Continues visiting {@link Node}s.
     */
    CONTINUE,

    /**
     * Stops visiting elements/entries of the {@link ListNode}/{@link MapNode}.
     */
    BREAK,

    /**
     * Skips the element/entry of the {@link ListNode}/{@link MapNode}
     */
    SKIP,

    /**
     * Stops visiting {@link Node}s.
     */
    STOP

}
