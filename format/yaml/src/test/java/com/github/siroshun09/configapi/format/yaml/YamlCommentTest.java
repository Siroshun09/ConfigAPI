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

package com.github.siroshun09.configapi.format.yaml;

import com.github.siroshun09.configapi.core.comment.SimpleComment;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.CommentableNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.StringValue;
import com.github.siroshun09.configapi.format.yaml.comment.YamlBlockComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlInlineComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlNodeComment;
import com.github.siroshun09.configapi.format.yaml.comment.YamlRootComment;
import com.github.siroshun09.configapi.test.shared.util.NodeAssertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.github.siroshun09.configapi.test.shared.util.Replacer.lines;

class YamlCommentTest {

    private static final String LOAD_AND_SAVE_WITH_COMMENTS_YAML =
            lines("""
                    # header
                                
                    # key
                    # block
                    # comment
                    test: true # inline
                    # footer
                    """);

    @Test
    void testLoadAndSaveWithComments() throws IOException {
        try (var reader = new StringReader(LOAD_AND_SAVE_WITH_COMMENTS_YAML)) {
            var expected = MapNode.create();
            expected.setComment(new YamlRootComment(new YamlBlockComment(" header", 0), new YamlBlockComment(" footer", 0)));
            expected.set("test", CommentableNode.withComment(BooleanValue.TRUE, new YamlNodeComment(new YamlBlockComment(lines(" key\n block\n comment"), 0), new YamlInlineComment(" inline"))));

            var loaded = YamlFormat.COMMENT_PROCESSING.load(reader);
            NodeAssertion.assertEquals(expected, loaded);

            try (var writer = new StringWriter()) {
                YamlFormat.COMMENT_PROCESSING.save(loaded, writer);
                Assertions.assertEquals(LOAD_AND_SAVE_WITH_COMMENTS_YAML, lines(writer.toString()));
            }
        }
    }

    @Test
    void testInlineComment() throws IOException {
        try (var writer = new StringWriter()) {
            var mapNode = MapNode.create();
            mapNode.set("key", CommentableNode.withComment(new StringValue("value"), SimpleComment.create("test", "inline")));
            YamlFormat.COMMENT_PROCESSING.save(mapNode, writer);
            Assertions.assertEquals(lines("key: value # test\n"), lines(writer.toString()));
        }
    }
}
