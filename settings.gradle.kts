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

pluginManagement {
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "configapi"

val prefix = rootProject.name

include("$prefix-core")
project(":$prefix-core").projectDir = file("core")

include("$prefix-test-shared-classes")
project(":$prefix-test-shared-classes").projectDir = file("test-shared-classes")

// file formats
sequenceOf(
    "binary",
    "gson",
    "jackson",
    "properties",
    "yaml"
).forEach {
    include("$prefix-format-$it")
    project(":$prefix-format-$it").projectDir = file("./format/$it")
}

include("$prefix-bom")
project(":$prefix-bom").projectDir = file("bom")
