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

package dev.siroshun.configapi.format.yaml;

import dev.siroshun.configapi.core.file.FileFormat;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.ArrayNode;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NullNode;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.ObjectNode;
import dev.siroshun.configapi.core.node.ValueNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A {@link FileFormat} implementation that loading/saving {@link MapNode} from/to yaml files.
 * <p>
 * Supported {@link Node}s:
 *
 * <ul>
 *     <li>{@link ValueNode}s
 *     <ul>
 *         <li>{@link CharValue} and {@link CharArray} is written as {@link String}</li>
 *         <li>{@link EnumValue} will be written as {@link String} using {@link Enum#name()}</li>
 *         <li>Loading {@link CharValue}, {@link CharArray}, and {@link EnumValue} is not supported</li>
 *     </ul>
 *     </li>
 *     <li>{@link MapNode} and {@link ListNode}</li>
 *     <li>{@link ArrayNode}s: serialize only</li>
 *     <li>{@link NullNode}</li>
 *     <li>{@link ObjectNode} that holds a basic Java object
 *     <ul>
 *         <li>The list of supported Java objects is in SafeRepresenter (snakeyaml)</li>
 *         <li>Other object types are not supported</li>
 *     </ul>
 *     </li>
 * </ul>
 */
public final class YamlFormat implements FileFormat<MapNode> {

    /**
     * An instance of default {@link YamlFormat}.
     */
    public static final YamlFormat DEFAULT = new YamlFormat.Builder().build();

    /**
     * An instance of {@link YamlFormat} that processes the comments.
     */
    public static final YamlFormat COMMENT_PROCESSING = new YamlFormat.Builder().processComment(true).build();

    /**
     * Creates a new {@link YamlFormat.Builder}.
     *
     * @return a new {@link YamlFormat.Builder}
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    private final ThreadLocal<YamlHolder> yamlHolder;

    private YamlFormat(@NotNull YamlParameter yamlParameter) {
        this.yamlHolder = ThreadLocal.withInitial(yamlParameter::createYamlHolder);
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        try {
            var yamlHolder = this.yamlHolder.get();
            return NodeConverter.toMapNode(yamlHolder.yaml().compose(reader), yamlHolder);
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        try {
            var yamlHolder = this.yamlHolder.get();
            yamlHolder.yaml().serialize(NodeConverter.toYamlNode(node, yamlHolder), writer);
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    /**
     * A builder of {@link YamlFormat}.
     */
    public static final class Builder {

        private DumperOptions.FlowStyle flowStyle = DumperOptions.FlowStyle.BLOCK;
        private DumperOptions.FlowStyle arrayFlowStyle = DumperOptions.FlowStyle.FLOW;
        private DumperOptions.FlowStyle sequenceFlowStyle = DumperOptions.FlowStyle.BLOCK;
        private DumperOptions.FlowStyle mapFlowStyle = DumperOptions.FlowStyle.BLOCK;
        private DumperOptions.ScalarStyle scalarStyle = DumperOptions.ScalarStyle.PLAIN;
        private int indent = 2;
        private boolean processComment;

        private Builder() {
        }

        /**
         * Sets {@link org.yaml.snakeyaml.DumperOptions.FlowStyle}.
         * <p>
         * Passing {@code null} to set the default style ({@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}.
         *
         * @param flowStyle {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} or null to set the default style
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder flowStyle(@Nullable DumperOptions.FlowStyle flowStyle) {
            this.flowStyle = flowStyle != null ? flowStyle : DumperOptions.FlowStyle.BLOCK;
            return this;
        }

        /**
         * Sets {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} for arrays.
         * <p>
         * Passing {@code null} to set the default style ({@link org.yaml.snakeyaml.DumperOptions.FlowStyle#FLOW}.
         * <p>
         * This style is only applied when the root style (set by {@link #flowStyle(DumperOptions.FlowStyle)} is {@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}
         *
         * @param flowStyle {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} or null to set the default style
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder arrayFlowStyle(@Nullable DumperOptions.FlowStyle flowStyle) {
            this.arrayFlowStyle = flowStyle != null ? flowStyle : DumperOptions.FlowStyle.FLOW;
            return this;
        }

        /**
         * Sets {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} for sequences.
         * <p>
         * Passing {@code null} to set the default style ({@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}.
         * <p>
         * This style is only applied when the root style (set by {@link #flowStyle(DumperOptions.FlowStyle)} is {@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}
         *
         * @param flowStyle {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} or null to set the default style
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder sequenceFlowStyle(@Nullable DumperOptions.FlowStyle flowStyle) {
            this.sequenceFlowStyle = flowStyle != null ? flowStyle : DumperOptions.FlowStyle.BLOCK;
            return this;
        }

        /**
         * Sets {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} for maps.
         * <p>
         * Passing {@code null} to set the default style ({@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}.
         * <p>
         * This style is only applied when the root style (set by {@link #flowStyle(DumperOptions.FlowStyle)} is {@link org.yaml.snakeyaml.DumperOptions.FlowStyle#BLOCK}
         *
         * @param flowStyle {@link org.yaml.snakeyaml.DumperOptions.FlowStyle} or null to set the default style
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder mapFlowStyle(@Nullable DumperOptions.FlowStyle flowStyle) {
            this.mapFlowStyle = flowStyle != null ? flowStyle : DumperOptions.FlowStyle.BLOCK;
            return this;
        }

        /**
         * Sets {@link org.yaml.snakeyaml.DumperOptions.ScalarStyle}.
         * <p>
         * Passing {@code null} to set the default style ({@link org.yaml.snakeyaml.DumperOptions.ScalarStyle#PLAIN}.
         *
         * @param scalarStyle {@link org.yaml.snakeyaml.DumperOptions.ScalarStyle} or null to set the default style
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder scalarStyle(@Nullable DumperOptions.ScalarStyle scalarStyle) {
            this.scalarStyle = scalarStyle != null ? scalarStyle : DumperOptions.ScalarStyle.PLAIN;
            return this;
        }

        /**
         * Sets number of spaces as indentation.
         * <p>
         * Passing 0 or negative value to set the default indent (2).
         *
         * @param indent number of spaces or 0/negative value to set default value
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder indent(int indent) {
            this.indent = 0 < indent ? indent : 2;
            return this;
        }

        /**
         * Sets whether to process comments.
         * <p>
         * Node that the comments will be processed when {@link #flowStyle(DumperOptions.FlowStyle)} is {@link DumperOptions.FlowStyle#BLOCK}
         *
         * @param processComment {@code true} to process comments, {@code false} to ignore comments
         * @return this {@link Builder} instance
         */
        @Contract("_ -> this")
        public @NotNull Builder processComment(boolean processComment) {
            this.processComment = processComment;
            return this;
        }

        /**
         * Builds {@link YamlFormat}.
         *
         * @return a created {@link YamlFormat}
         */
        public @NotNull YamlFormat build() {
            return new YamlFormat(new YamlParameter(this.flowStyle, this.arrayFlowStyle, this.sequenceFlowStyle, this.mapFlowStyle, this.scalarStyle, this.indent, this.processComment));
        }
    }
}
