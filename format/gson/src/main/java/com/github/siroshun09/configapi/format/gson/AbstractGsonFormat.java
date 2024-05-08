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

package com.github.siroshun09.configapi.format.gson;

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.Node;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

abstract class AbstractGsonFormat<N extends Node<?>> implements FileFormat<N> {

    private final Gson gson;
    private final Class<N> nodeClass;

    protected AbstractGsonFormat(@NotNull GsonBuilder builder, @NotNull Class<N> nodeClass, @NotNull TypeAdapter<N> adapter) {
        this.gson = builder.registerTypeAdapter(nodeClass, adapter).create();
        this.nodeClass = nodeClass;
    }

    @Override
    public @NotNull N load(@NotNull Reader reader) throws IOException {
        try {
            var node = this.gson.fromJson(reader, this.nodeClass);
            return node != null ? node : this.createEmptyNode();
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void save(@NotNull N node, @NotNull Writer writer) throws IOException {
        try {
            this.gson.toJson(node, this.nodeClass, writer);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    protected abstract @NotNull N createEmptyNode();
}
