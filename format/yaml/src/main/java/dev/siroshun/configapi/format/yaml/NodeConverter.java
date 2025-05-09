/*
 *     Copyright 2025 Siroshun09
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

package dev.siroshun.configapi.format.yaml;

import dev.siroshun.configapi.core.comment.Comment;
import dev.siroshun.configapi.core.comment.SimpleComment;
import dev.siroshun.configapi.core.node.ArrayNode;
import dev.siroshun.configapi.core.node.CommentableNode;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NullNode;
import dev.siroshun.configapi.format.yaml.comment.YamlBlockComment;
import dev.siroshun.configapi.format.yaml.comment.YamlInlineComment;
import dev.siroshun.configapi.format.yaml.comment.YamlNodeComment;
import dev.siroshun.configapi.format.yaml.comment.YamlRootComment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class NodeConverter {

    static @NotNull MapNode toMapNode(@Nullable org.yaml.snakeyaml.nodes.Node root, @NotNull YamlHolder yamlHolder) throws IOException {
        if (root instanceof MappingNode mappingNode) {
            if (mappingNode.getValue().isEmpty()) {
                return MapNode.create();
            }

            if (yamlHolder.parameter().processComment()) {
                var first = mappingNode.getValue().getFirst().getKeyNode();
                var blockComments = first.getBlockComments();

                if (blockComments != null) {
                    int lastBlank = -1;

                    for (int i = blockComments.size() - 1; 0 <= i; i--) {
                        if (blockComments.get(i).getCommentType() == CommentType.BLANK_LINE) {
                            lastBlank = i;
                            break;
                        }
                    }

                    if (lastBlank != -1) {
                        mappingNode.setBlockComments(blockComments.subList(0, lastBlank));
                        first.setBlockComments(blockComments.subList(lastBlank + 1, blockComments.size()));
                    }
                }
            }

            var mapNode = (MapNode) toNode(mappingNode, yamlHolder);

            if (yamlHolder.parameter().processComment()) {
                mapNode.setComment(processRootComment(mappingNode));
            }

            return mapNode;
        } else if (root == null) {
            return MapNode.create();
        } else {
            throw new IOException("Unsupported root type: " + root.getClass().getSimpleName());
        }
    }

    private static @NotNull Node<?> toNode(@NotNull org.yaml.snakeyaml.nodes.Node node, @NotNull YamlHolder yamlHolder) throws IOException {
        var constructor = yamlHolder.constructor();

        switch (node) {
            case MappingNode mappingNode -> {
                var mapNode = MapNode.create();

                constructor.flattenMapping(mappingNode);

                for (var tuple : mappingNode.getValue()) {
                    var key = constructor.constructObject(tuple.getKeyNode());
                    var value = toNode(tuple.getValueNode(), yamlHolder);

                    if (yamlHolder.parameter().processComment()) {
                        if (value instanceof ListNode || value instanceof MapNode) {
                            ((CommentableNode<?>) value).setComment(processComment(tuple.getKeyNode(), tuple.getKeyNode()));
                            mapNode.set(key, value);
                        } else {
                            var comment = processComment(tuple.getKeyNode(), tuple.getValueNode());
                            mapNode.set(key, comment != null ? CommentableNode.withComment(value, comment) : value);
                        }
                    } else {
                        mapNode.set(key, value);
                    }
                }

                return mapNode;
            }
            case SequenceNode sequenceNode -> {
                var nodes = sequenceNode.getValue();
                var listNode = ListNode.create(nodes.size());

                for (var element : nodes) {
                    var converted = toNode(element, yamlHolder);

                    if (yamlHolder.parameter().processComment()) {
                        var commented = CommentableNode.withComment(converted, processComment(element));
                        listNode.add(commented.hasComment() ? commented : converted);
                    } else {
                        listNode.add(converted);
                    }
                }

                return listNode;
            }
            case ScalarNode scalarNode -> {
                return Node.fromObject(constructor.constructObject(node));
            }
            case AnchorNode anchorNode -> {
                return toNode(anchorNode.getRealNode(), yamlHolder);
            }
            default -> throw new IOException("Unsupported node: " + node);
        }
    }

    private static @Nullable Comment processRootComment(@NotNull org.yaml.snakeyaml.nodes.Node node) {
        var block = processBlockComment(node.getBlockComments());
        var end = processBlockComment(node.getEndComments());
        return block != null || end != null ? new YamlRootComment(block, end) : null;
    }

    private static @Nullable Comment processComment(@NotNull org.yaml.snakeyaml.nodes.Node node) {
        var block = processBlockComment(node.getBlockComments());
        var inline = processInlineComment(node.getInLineComments());
        return block != null || inline != null ? new YamlNodeComment(block, inline) : null;
    }

    private static @Nullable Comment processComment(@NotNull org.yaml.snakeyaml.nodes.Node blockSource, @NotNull org.yaml.snakeyaml.nodes.Node inlineSource) {
        var block = processBlockComment(blockSource.getBlockComments());
        var inline = processInlineComment(inlineSource.getInLineComments());
        return block != null || inline != null ? new YamlNodeComment(block, inline) : null;
    }

    private static @Nullable YamlInlineComment processInlineComment(@Nullable List<CommentLine> commentLines) {
        if (commentLines == null || commentLines.isEmpty()) {
            return null;
        }

        StringBuilder builder = null;

        for (CommentLine commentLine : commentLines) {
            if (builder == null) {
                builder = new StringBuilder(commentLine.getValue());
            } else {
                builder.append(System.lineSeparator()).append(commentLine.getValue());
            }
        }

        return new YamlInlineComment(builder.toString());
    }

    private static @Nullable YamlBlockComment processBlockComment(@Nullable List<CommentLine> commentLines) {
        if (commentLines == null || commentLines.isEmpty()) {
            return null;
        }

        int prependBlankLines = 0;
        StringBuilder builder = null;

        for (CommentLine commentLine : commentLines) {
            if (commentLine.getCommentType() == CommentType.BLANK_LINE) {
                if (builder != null) {
                    builder.append(System.lineSeparator());
                } else {
                    prependBlankLines++;
                }
            } else {
                if (builder == null) {
                    builder = new StringBuilder(commentLine.getValue());
                } else {
                    builder.append(System.lineSeparator()).append(commentLine.getValue());
                }
            }
        }

        return builder != null ? new YamlBlockComment(builder.toString(), prependBlankLines) : null;
    }

    static @NotNull org.yaml.snakeyaml.nodes.Node toYamlNode(@NotNull MapNode mapNode, @NotNull YamlHolder yamlHolder) {
        var mappingNode = (MappingNode) toNode(mapNode, yamlHolder);

        applyComments(mapNode, mappingNode, mappingNode);

        if (mapNode.hasComment() && mapNode.getComment() instanceof YamlRootComment(
                YamlBlockComment header, YamlBlockComment footer
        )) {
            mappingNode.setBlockComments(toHeaderCommentLines(header));
            mappingNode.setEndComments(toCommentLines(footer));
        }

        mappingNode.setFlowStyle(yamlHolder.parameter().defaultFlowStyle());

        return mappingNode;
    }

    private static @NotNull org.yaml.snakeyaml.nodes.Node toNode(@NotNull Node<?> node, @NotNull YamlHolder yamlHolder) {
        switch (node) {
            case MapNode mapNode -> {
                var entries = mapNode.value().entrySet();
                var nodes = new ArrayList<NodeTuple>(entries.size());

                for (var entry : entries) {
                    var key = entry.getKey();
                    var value = entry.getValue();
                    var yKey = yamlHolder.representer().represent(key);
                    var yValue = toNode(value, yamlHolder);

                    applyComments(value, yKey, (value instanceof ListNode || value instanceof MapNode) ? yKey : yValue);
                    nodes.add(new NodeTuple(yKey, yValue));
                }

                return new MappingNode(Tag.MAP, nodes, yamlHolder.parameter().mapFlowStyle());
            }
            case ListNode listNode -> {
                var list = listNode.value();
                var nodes = new ArrayList<org.yaml.snakeyaml.nodes.Node>();

                for (var element : list) {
                    var yNode = toNode(element, yamlHolder);
                    applyComments(element, yNode, yNode);
                    nodes.add(yNode);
                }

                return new SequenceNode(Tag.SEQ, nodes, yamlHolder.parameter().sequenceFlowStyle());
            }
            case CommentedNode<?> commentedNode -> {
                return toNode(commentedNode.node(), yamlHolder);
            }
            case EnumValue<?>(Enum<?> value) -> {
                return yamlHolder.representer().represent(value.name());
            }
            case NullNode nullNode -> {
                return yamlHolder.representer().represent(null);
            }
            default -> {
                var represented = yamlHolder.representer().represent(node.value());
                if (node instanceof ArrayNode<?> && represented instanceof SequenceNode sequenceNode) {
                    sequenceNode.setFlowStyle(yamlHolder.parameter().arrayFlowStyle());
                }
                return represented;
            }
        }
    }

    private static @Nullable List<CommentLine> toHeaderCommentLines(@Nullable YamlBlockComment header) {
        if (header == null) {
            return null;
        }

        var comments = header.content().lines().toList();
        var commentLines = new ArrayList<CommentLine>(comments.size() + header.prependBlankLines() + 1);

        for (int i = 0; i < header.prependBlankLines(); i++) {
            commentLines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));
        }

        for (var comment : comments) {
            commentLines.add(new CommentLine(null, null, comment, CommentType.BLOCK));
        }

        commentLines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));

        return commentLines;
    }

    private static @Nullable List<CommentLine> toCommentLines(@Nullable YamlBlockComment blockComment) {
        if (blockComment == null) {
            return null;
        }

        var comments = blockComment.content().lines().toList();
        var commentLines = new ArrayList<CommentLine>(comments.size() + blockComment.prependBlankLines());

        for (int i = 0; i < blockComment.prependBlankLines(); i++) {
            commentLines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));
        }

        for (var comment : comments) {
            commentLines.add(new CommentLine(null, null, comment, CommentType.BLOCK));
        }

        return commentLines;
    }

    private static @Nullable List<CommentLine> toCommentLines(@Nullable YamlInlineComment inlineComment) {
        if (inlineComment == null) {
            return null;
        }

        var comments = inlineComment.content().lines().toList();
        var commentLines = new ArrayList<CommentLine>(comments.size());

        for (var comment : comments) {
            commentLines.add(new CommentLine(null, null, comment, CommentType.IN_LINE));
        }

        return commentLines;
    }

    private static void applyComments(@NotNull Node<?> node, @NotNull org.yaml.snakeyaml.nodes.Node blockTarget, @NotNull org.yaml.snakeyaml.nodes.Node inlineTarget) {
        if (!(node instanceof CommentableNode<?> commentableNode) || !commentableNode.hasComment()) {
            return;
        }

        Comment comment = commentableNode.getComment();

        switch (comment) {
            case YamlBlockComment blockComment -> blockTarget.setBlockComments(toCommentLines(blockComment));
            case YamlInlineComment inlineComment -> inlineTarget.setInLineComments(toCommentLines(inlineComment));
            case YamlNodeComment(YamlBlockComment block, YamlInlineComment inline) -> {
                blockTarget.setBlockComments(toCommentLines(block));
                inlineTarget.setInLineComments(toCommentLines(inline));
            }
            case SimpleComment simpleComment -> {
                boolean inline = simpleComment.type().equalsIgnoreCase(YamlInlineComment.TYPE);
                CommentType type = inline ? CommentType.IN_LINE : CommentType.BLOCK;

                var lines = simpleComment.content().lines().toList();
                var commentLines = new ArrayList<CommentLine>(lines.size());

                for (var line : lines) {
                    var value = line.isEmpty() ? "" : " " + line;
                    commentLines.add(new CommentLine(null, null, value, type));
                }

                if (inline) {
                    inlineTarget.setInLineComments(commentLines);
                } else {
                    blockTarget.setBlockComments(commentLines);
                }
            }
            default -> {
            }
        }
    }

    private NodeConverter() {
        throw new UnsupportedOperationException();
    }
}
