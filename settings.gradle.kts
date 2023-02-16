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

// file formats
sequenceOf(
    "yaml"
).forEach {
    include("$prefix-$it")
    project(":$prefix-$it").projectDir = file("./format/$it")
}
