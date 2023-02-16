plugins {
    // This will show as an error on the IDE, but can be compiled successfully.
    // See https://github.com/gradle/gradle/issues/22797
    alias(libs.plugins.aggregate.javadoc)
}

tasks {
    aggregateJavadoc {
        (options as StandardJavadocDocletOptions)
            .docTitle("ConfigAPI $version")
            .windowTitle("ConfigAPI $version")
    }
}
