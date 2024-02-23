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

package com.github.siroshun09.configapi.core.comment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An interface that has the comment as a {@link String}.
 */
public interface SimpleComment extends Comment {

    /**
     * Creates a new {@link SimpleComment}.
     *
     * @param content the content
     * @return a new {@link SimpleComment}
     */
    @Contract("_ -> new")
    static @NotNull SimpleComment create(@NotNull String content) {
        return create(content, "");
    }

    /**
     * Creates a new {@link SimpleComment}.
     *
     * @param content the content
     * @param type the type of the comment
     * @return a new {@link SimpleComment}
     */
    @Contract("_, _ -> new")
    static @NotNull SimpleComment create(@NotNull String content, @NotNull String type) {
        return new SimpleCommentImpl(Objects.requireNonNull(content), Objects.requireNonNull(type));
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    @NotNull String content();

    /**
     * Returns the type of this comment.
     *
     * @return the type of this comment
     */
    default @NotNull String type() {
        return "";
    }
}
