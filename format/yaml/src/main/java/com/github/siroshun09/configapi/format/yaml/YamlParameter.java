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

package com.github.siroshun09.configapi.format.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.FLOW;

record YamlParameter(
        @NotNull DumperOptions.FlowStyle defaultFlowStyle,
        @NotNull DumperOptions.FlowStyle arrayFlowStyle,
        @NotNull DumperOptions.FlowStyle sequenceFlowStyle,
        @NotNull DumperOptions.FlowStyle mapFlowStyle,
        @NotNull DumperOptions.ScalarStyle scalarStyle,
        int indent,
        boolean processComment
) {

    @Override
    public @NotNull DumperOptions.FlowStyle arrayFlowStyle() {
        return this.defaultFlowStyle == BLOCK ? this.arrayFlowStyle : FLOW;
    }

    @Override
    public @NotNull DumperOptions.FlowStyle sequenceFlowStyle() {
        return this.defaultFlowStyle == BLOCK ? this.sequenceFlowStyle : FLOW;
    }

    @Override
    public @NotNull DumperOptions.FlowStyle mapFlowStyle() {
        return this.defaultFlowStyle == BLOCK ? this.mapFlowStyle : FLOW;
    }

    public YamlHolder createYamlHolder() {
        var dumperOptions = this.createDumperOptions();
        var loaderOptions = this.createLoaderOptions();
        var constructor = this.createConstructor(loaderOptions);
        var representer = this.createRepresenter(dumperOptions);

        return new YamlHolder(new Yaml(constructor, representer, dumperOptions, loaderOptions), constructor, representer, this);
    }

    private @NotNull LoaderOptions createLoaderOptions() {
        var loaderOptions = new LoaderOptions();

        loaderOptions.setCodePointLimit(Integer.MAX_VALUE);
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setProcessComments(this.defaultFlowStyle == BLOCK && this.processComment);

        return loaderOptions;
    }

    private @NotNull DumperOptions createDumperOptions() {
        var dumperOptions = new DumperOptions();

        dumperOptions.setDefaultFlowStyle(this.defaultFlowStyle);
        dumperOptions.setDefaultScalarStyle(this.scalarStyle);
        dumperOptions.setIndent(this.indent);
        dumperOptions.setProcessComments(this.defaultFlowStyle == BLOCK && this.processComment);

        return dumperOptions;
    }

    private @NotNull ObjectConstructor createConstructor() {
        return this.createConstructor(this.createLoaderOptions());
    }

    private @NotNull ObjectConstructor createConstructor(@NotNull LoaderOptions loaderOptions) {
        return new ObjectConstructor(loaderOptions);
    }

    private @NotNull Representer createRepresenter() {
        return this.createRepresenter(this.createDumperOptions());
    }

    private @NotNull Representer createRepresenter(@NotNull DumperOptions dumperOptions) {
        var representer = new Representer(dumperOptions);
        representer.setDefaultFlowStyle(this.defaultFlowStyle);
        representer.setDefaultScalarStyle(this.scalarStyle);
        return representer;
    }
}
