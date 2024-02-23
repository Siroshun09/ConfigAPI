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

import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CommentedNode;
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
import com.github.siroshun09.configapi.core.node.ObjectNode;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import org.jetbrains.annotations.NotNull;

/**
 * A visitor of {@link Node}s.
 * <p>
 * This interface can be used through {@link Node#accept(NodeVisitor)}.
 */
public interface NodeVisitor {

    /**
     * Visits a {@link StringValue}.
     *
     * @param value a {@link StringValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull StringValue value);

    /**
     * Visits a {@link BooleanArray}.
     *
     * @param array a {@link BooleanArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull BooleanArray array);

    /**
     * Visits a {@link BooleanValue}.
     *
     * @param value a {@link BooleanValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull BooleanValue value);

    /**
     * Visits a {@link ByteArray}.
     *
     * @param array a {@link ByteArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull ByteArray array);

    /**
     * Visits a {@link ByteValue}.
     *
     * @param value a {@link ByteValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull ByteValue value);

    /**
     * Visits a {@link DoubleArray}.
     *
     * @param array a {@link DoubleArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull DoubleArray array);

    /**
     * Visits a {@link DoubleValue}.
     *
     * @param value a {@link DoubleValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull DoubleValue value);

    /**
     * Visits a {@link FloatArray}.
     *
     * @param array a {@link FloatArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull FloatArray array);

    /**
     * Visits a {@link FloatValue}.
     *
     * @param value a {@link FloatValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull FloatValue value);

    /**
     * Visits a {@link IntArray}.
     *
     * @param array a {@link IntArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull IntArray array);

    /**
     * Visits a {@link IntValue}.
     *
     * @param value a {@link IntValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull IntValue value);

    /**
     * Visits a {@link LongArray}.
     *
     * @param array a {@link LongArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull LongArray array);

    /**
     * Visits a {@link LongValue}.
     *
     * @param value a {@link LongValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull LongValue value);

    /**
     * Visits a {@link ShortArray}.
     *
     * @param array a {@link ShortArray} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull ShortArray array);

    /**
     * Visits a {@link ShortValue}.
     *
     * @param value a {@link ShortValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull ShortValue value);

    /**
     * Visits a {@link EnumValue}.
     *
     * @param value a {@link EnumValue} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull EnumValue<?> value);

    /**
     * Visits a {@link ObjectNode}.
     *
     * @param node a {@link ObjectNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull ObjectNode<?> node);

    /**
     * Starts visiting a {@link ListNode}.
     * <p>
     * If this method returns {@link VisitResult#SKIP} or {@link VisitResult#STOP}, {@link ListNode#accept(NodeVisitor)} will not visit its elements.
     *
     * @param node a {@link ListNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult startList(@NotNull ListNode node);

    /**
     * Visits {@link ListNode}'s element.
     * <p>
     * If this method returns:
     *
     * <ul>
     *     <li>{@link VisitResult#CONTINUE} - Calls {@link Node#accept(NodeVisitor)} of the element</li>
     *     <li>{@link VisitResult#BREAK} - Stops visiting elements and calls {@link NodeVisitor#endList(ListNode)}</li>
     *     <li>{@link VisitResult#SKIP} - Skips the element ({@link Node#accept(NodeVisitor)} of the element will not be called)</li>
     *     <li>{@link VisitResult#STOP} - Stop visiting immediately and {@link ListNode#accept(NodeVisitor)} will returns {@link VisitResult#STOP}</li>
     * </ul>
     *
     * @param index the index of the element in the list
     * @param node  the element
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visitElement(int index, @NotNull Node<?> node);

    /**
     * Ends visiting a {@link ListNode}.
     *
     * @param node a {@link ListNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult endList(@NotNull ListNode node);

    /**
     * Starts visiting a {@link MapNode}.
     * <p>
     * If this method returns {@link VisitResult#SKIP} or {@link VisitResult#STOP}, {@link MapNode#accept(NodeVisitor)} will not visit its entries.
     *
     * @param node a {@link MapNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult startMap(@NotNull MapNode node);

    /**
     * Visits {@link MapNode}'s entry.
     * <p>
     * If this method returns:
     *
     * <ul>
     *     <li>{@link VisitResult#CONTINUE} - Calls {@link Node#accept(NodeVisitor)} of the entry</li>
     *     <li>{@link VisitResult#BREAK} - Stops visiting entries and calls {@link NodeVisitor#endMap(MapNode)}</li>
     *     <li>{@link VisitResult#SKIP} - Skips the entry ({@link Node#accept(NodeVisitor)} of the entry will not be called)</li>
     *     <li>{@link VisitResult#STOP} - Stop visiting immediately and {@link MapNode#accept(NodeVisitor)} will returns {@link VisitResult#STOP}</li>
     * </ul>
     *
     * @param num  the number of the entry in the map
     * @param key the key of the entry
     * @param node the value of the entry
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visitEntry(int num, @NotNull Object key, @NotNull Node<?> node);

    /**
     * Ends visiting a {@link MapNode}.
     *
     * @param node a {@link MapNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult endMap(@NotNull MapNode node);

    /**
     * Visits a {@link CommentedNode}.
     * <p>
     * If this method returns {@link VisitResult#CONTINUE},
     * {@link CommentedNode#accept(NodeVisitor)} will call {@link Node#accept(NodeVisitor)} of {@link CommentedNode#node()}
     * and returns its result.
     *
     * @param node a {@link CommentedNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull CommentedNode<?> node);

    /**
     * Visits a {@link NullNode}.
     *
     * @param node a {@link NullNode} being visited
     * @return a {@link VisitResult}
     */
    @NotNull VisitResult visit(@NotNull NullNode node);

}
