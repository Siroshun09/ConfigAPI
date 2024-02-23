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

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

class ObjectConstructor extends SafeConstructor {

    /**
     * Create an instance
     *
     * @param loaderOptions - the configuration options
     */
    ObjectConstructor(LoaderOptions loaderOptions) {
        super(loaderOptions);
    }

    @Override
    public Object constructObject(Node node) {
        return super.constructObject(node);
    }

    @Override
    public void flattenMapping(MappingNode node) {
        super.flattenMapping(node);
    }
}
