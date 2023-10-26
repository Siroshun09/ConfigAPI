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
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
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

    private final ThreadLocal<Yaml> yaml;

    private YamlFormat(@NotNull ThreadLocal<Yaml> yaml) {
        this.yaml = yaml;
    }

    @Override
    public @NotNull MapNode load(@NotNull Reader reader) throws IOException {
        try {
            return this.toMapNode(this.yaml.get().load(reader));
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void save(@NotNull MapNode node, @NotNull Writer writer) throws IOException {
        try {
            this.yaml.get().dump(node, writer);
        } catch (YAMLException e) {
            throw new IOException(e);
        }
    }

    private @NotNull MapNode toMapNode(Object obj) {
        if (obj instanceof MapNode mapNode) {
            return mapNode;
        } else if (obj instanceof Map<?, ?> map) {
            return MapNode.create(map);
        } else {
            return MapNode.create();
        }
    }

    /**
     * A builder of {@link YamlFormat}.
     */
    public static class Builder {

        private DumperOptions.FlowStyle flowStyle = DumperOptions.FlowStyle.BLOCK;
        private DumperOptions.ScalarStyle scalarStyle = DumperOptions.ScalarStyle.PLAIN;
        private int indent = 2;

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
         * Builds {@link YamlFormat}.
         *
         * @return a created {@link YamlFormat}
         */
        public @NotNull YamlFormat build() {
            return new YamlFormat(ThreadLocal.withInitial(new YamlFactory(this.flowStyle, this.scalarStyle, this.indent)));
        }
    }

    private record YamlFactory(@NotNull DumperOptions.FlowStyle flowStyle,
                               @NotNull DumperOptions.ScalarStyle scalarStyle,
                               int indent) implements Supplier<Yaml> {
        @Override
        public Yaml get() {
            var dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(this.flowStyle);
            dumperOptions.setIndent(this.indent);

            var representer = new NodeRepresenter(dumperOptions);
            representer.setDefaultFlowStyle(this.flowStyle);

            var loaderOptions = new LoaderOptions();

            loaderOptions.setProcessComments(false);
            loaderOptions.setCodePointLimit(Integer.MAX_VALUE);

            var constructor = new Constructor(LinkedHashMap.class, loaderOptions);

            return new Yaml(constructor, representer, dumperOptions, loaderOptions);
        }
    }

    private static final class NodeRepresenter extends Representer {
        private NodeRepresenter(DumperOptions options) {
            super(options);

            this.representers.put(MapNode.IMPLEMENTATION_CLASS, data -> representMapping(Tag.MAP, ((MapNode) data).value(), options.getDefaultFlowStyle()));
            this.representers.put(ListNode.IMPLEMENTATION_CLASS, data -> representSequence(Tag.SEQ, ((ListNode) data).value(), options.getDefaultFlowStyle()));
            this.representers.put(EnumValue.class, data -> representData(((EnumValue<?>) data).value().name()));
            this.representers.put(NullNode.class, data -> this.nullRepresenter.representData(null));
            this.multiRepresenters.put(Node.class, data -> representData(((Node<?>) data).value()));
        }
    }
}
