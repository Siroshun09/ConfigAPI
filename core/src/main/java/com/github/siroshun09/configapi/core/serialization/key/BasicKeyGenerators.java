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

package com.github.siroshun09.configapi.core.serialization.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BasicKeyGenerators {

    static @NotNull String convertCamel(@Nullable String source, char delimiter) {
        if (source == null || source.isEmpty()) {
            return "";
        }

        int firstCodePoint = source.codePointAt(0);
        StringBuilder result = new StringBuilder();

        result.appendCodePoint(Character.toLowerCase(firstCodePoint));

        // 0: not uppercase, not number 1: uppercase 2: number 3: consecutive uppercase
        int previous = Character.isUpperCase(firstCodePoint) ? 1 : 0;
        boolean isConsecutiveUpperCase = false;

        for (int i = 1; i < source.codePointCount(0, source.length()); i++) {
            int codePoint = source.codePointAt(i);

            if (Character.isUpperCase(codePoint)) {
                int lowercase = Character.toLowerCase(codePoint);
                if ((previous & 1) == 1) {
                    result.appendCodePoint(lowercase);
                    previous = 3;
                } else {
                    result.append(delimiter).appendCodePoint(lowercase);
                    previous = 1;
                }
            } else if (Character.isDigit(codePoint)) {
                if (previous != 2) {
                    result.append(delimiter);
                    previous = 2;
                }
                result.appendCodePoint(codePoint);
            } else {
                if (previous == 2) {
                    result.append(delimiter);
                }

                if (previous == 3) {
                    result.insert(i, delimiter);
                }

                previous = 0;
                result.appendCodePoint(codePoint);
            }
        }

        return result.toString();
    }

    private BasicKeyGenerators() {
        throw new UnsupportedOperationException();
    }
}
