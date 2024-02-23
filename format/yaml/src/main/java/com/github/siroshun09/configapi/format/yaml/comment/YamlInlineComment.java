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

package com.github.siroshun09.configapi.format.yaml.comment;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A {@link SimpleComment} implementation that holds the inline comments in Yaml.
 *
 * @param content the content of the comment
 */
public record YamlInlineComment(@NotNull String content) implements SimpleComment {

    /**
     * The type of this comment.
     */
    public static final String TYPE = "inline";

    public YamlInlineComment {
        Objects.requireNonNull(content);
    }

    @Override
    public @NotNull String type() {
        return TYPE;
    }
}
