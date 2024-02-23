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

package com.github.siroshun09.configapi.test.shared.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A utility class to replace strings.
 */
public final class Replacer {

    private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\n|\\r");

    /**
     * Replaces the line separators that are contained in the specified {@link String} with {@link System#lineSeparator()}.
     *
     * @param str the {@link String} to replace
     * @return the replaced {@link String}
     */
    public static @NotNull String lines(@NotNull String str) {
        return LINE_PATTERN.matcher(str).replaceAll(System.lineSeparator());
    }

    private Replacer() {
        throw new UnsupportedOperationException();
    }
}
