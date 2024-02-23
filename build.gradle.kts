plugins {
    id("configapi.aggregate-javadoc")
}

tasks {
    register<Delete>("clean") {
        group = "build"
        layout.buildDirectory.get().asFile.deleteRecursively()
    }
}
