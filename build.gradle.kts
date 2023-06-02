plugins {
    alias(libs.plugins.aggregate.javadoc)
}

tasks {
    aggregateJavadoc {
        (options as StandardJavadocDocletOptions)
            .docTitle("ConfigAPI $version")
            .windowTitle("ConfigAPI $version")
    }
}
