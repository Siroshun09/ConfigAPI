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

plugins {
    alias(libs.plugins.jcommon)
    alias(libs.plugins.aggregated.javadoc)
    alias(libs.plugins.mavenPublication)
    alias(libs.plugins.mavenCentralPortal)
}

jcommon {
    javaVersion = JavaVersion.VERSION_17

    commonDependencies {
        compileOnlyApi(libs.annotations)

        testImplementation(platform(libs.junit.bom))
        testImplementation(libs.junit.jupiter)
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

mavenPublication {
    localRepository(mavenCentralPortal.stagingDirectory)
    description("A configuration library for Java.")
    apacheLicense()
    developer("Siroshun09")
    github("Siroshun09/ConfigAPI")
}
