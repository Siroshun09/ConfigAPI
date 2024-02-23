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

import aggregator.JavadocAggregator

tasks {
    create<Javadoc>(JavadocAggregator.AGGREGATE_JAVADOC_TASK_NAME) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        setDestinationDir(layout.buildDirectory.dir("docs").get().asFile)
        classpath = objects.fileCollection()
        doFirst {
            include(JavadocAggregator.includes)
            exclude(JavadocAggregator.excludes)

            val opts = options as StandardJavadocDocletOptions
            opts.docTitle("ConfigAPI $version")
                    .windowTitle("ConfigAPI $version")
                    .links(*JavadocAggregator.javadocLinks.toTypedArray())

            opts.addStringOption("Xmaxwarns", Int.MAX_VALUE.toString())
        }
    }
}
