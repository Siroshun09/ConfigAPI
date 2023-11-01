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

package com.github.siroshun09.configapi.core.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * A {@link Node} implementation that holds an int array.
 *
 * @param value an int array
 */
public record IntArray(int @NotNull [] value) implements ArrayNode<int[]> {

    /**
     * @param value an int array
     */
    public IntArray {
        Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IntArray) obj;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public String toString() {
        return "IntArray[" +
                "value=" + Arrays.toString(this.value) +
                ']';
    }

    @Override
    public void appendValue(@NotNull StringBuilder builder) {
        int iMax = this.value.length - 1;
        if (iMax == -1) {
            builder.append("[]");
        } else {
            builder.append('[');

            for (int i = 0; i <= iMax; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(this.value[i]);
            }

            builder.append(']');
        }
    }
}
