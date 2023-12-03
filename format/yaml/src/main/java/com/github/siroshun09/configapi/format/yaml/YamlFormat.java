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

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;

/**
 * A {@link FileFormat} implementation that loading/saving {@link MapNode} from/to yaml files.
 * <p>
 * Supported {@link Node}s:
 *
 * <ul>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ValueNode}s
 *     <ul>
 *         <li>{@link EnumValue} will be written as {@link String} using {@link Enum#name()}</li>
 *         <li>Loading {@link EnumValue} is not supported</li>
 *     </ul>
 *     </li>
 *     <li>{@link MapNode} and {@link ListNode}</li>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ArrayNode}s: serialize only</li>
 *     <li>{@link NullNode}</li>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ObjectNode} that holds a basic Java object
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

    private final ThreadLocal<Yaml> yaml;
    private final Supplier<ObjectConstructor> objectConstructorSupplier;
    private final Supplier<Representer> representerSupplier;

    private YamlFormat(@NotNull YamlFactory yamlFactory) {
        this.yaml = ThreadLocal.withInitial(yamlFactory);
        this.objectConstructorSupplier = yamlFactory::objectConstructor;
        this.representerSupplier = yamlFactory::representer;
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        try {
            return NodeConverter.toMapNode(this.yaml.get().compose(reader), this.objectConstructorSupplier.get());
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        try {
            this.yaml.get().serialize(NodeConverter.toYamlNode(node, this.representerSupplier.get()), writer);
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    /**
     * A builder of {@link YamlFormat}.
     */
    public static class Builder {

        private DumperOptions.FlowStyle flowStyle = DumperOptions.FlowStyle.BLOCK;
        private DumperOptions.ScalarStyle scalarStyle = DumperOptions.ScalarStyle.PLAIN;
        private int indent = 2;
        private boolean processComment;

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
            return new YamlFormat(new YamlFactory(this.flowStyle, this.scalarStyle, this.indent, this.processComment));
        }
    }

    private record YamlFactory(@NotNull DumperOptions.FlowStyle flowStyle,
                               @NotNull DumperOptions.ScalarStyle scalarStyle,
                               int indent,
                               boolean processComment) implements Supplier<Yaml> {
        @Override
        public Yaml get() {
            var loaderOptions = this.loaderOptions();
            var dumperOptions = this.dumperOptions();
            return new Yaml(objectConstructor(loaderOptions), representer(dumperOptions), dumperOptions, loaderOptions);
        }

        private @NotNull LoaderOptions loaderOptions() {
            var loaderOptions = new LoaderOptions();

            loaderOptions.setCodePointLimit(Integer.MAX_VALUE);
            loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
            loaderOptions.setProcessComments(this.processComment);

            return loaderOptions;
        }

        private @NotNull DumperOptions dumperOptions() {
            var dumperOptions = new DumperOptions();

            dumperOptions.setDefaultFlowStyle(this.flowStyle);
            dumperOptions.setIndent(this.indent);
            dumperOptions.setProcessComments(this.processComment);

            return dumperOptions;
        }

        private @NotNull ObjectConstructor objectConstructor() {
            return this.objectConstructor(this.loaderOptions());
        }

        private @NotNull ObjectConstructor objectConstructor(@NotNull LoaderOptions loaderOptions) {
            return new ObjectConstructor(loaderOptions);
        }

        private @NotNull Representer representer() {
            return this.representer(this.dumperOptions());
        }

        private @NotNull Representer representer(@NotNull DumperOptions dumperOptions) {
            var representer = new Representer(dumperOptions);
            representer.setDefaultFlowStyle(this.flowStyle);
            return representer;
        }
    }
}
