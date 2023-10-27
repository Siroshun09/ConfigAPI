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
    "gson",
    "yaml"
).forEach {
    include("$prefix-format-$it")
    project(":$prefix-format-$it").projectDir = file("./format/$it")
}
