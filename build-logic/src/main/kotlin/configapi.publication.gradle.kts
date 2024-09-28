plugins {
    id("configapi.common-conventions")
    `maven-publish`
    signing
}

val stagingDir =rootProject.layout.buildDirectory.dir("staging")

tasks {
    publish {
        stagingDir.get().asFile.deleteRecursively()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            java {
                withJavadocJar()
                withSourcesJar()
            }

            groupId = project.group.toString()
            artifactId = project.name

            from(components["java"])

            pom {
                name.set(project.name)
                url.set("https://github.com/Siroshun09/ConfigAPI")
                description.set("A configuration library for Java.")

                licenses {
                    license {
                        name.set("APACHE LICENSE, VERSION 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("Siroshun09")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/Siroshun09/ConfigAPI.git")
                    developerConnection.set("scm:git@github.com:Siroshun09/ConfigAPI.git")
                    url.set("https://github.com/Siroshun09/ConfigAPI")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/Siroshun09/ConfigAPI/issues")
                }

                ciManagement {
                    system.set("GitHub Actions")
                    url.set("https://github.com/Siroshun09/ConfigAPI/runs")
                }
            }
        }

        repositories {
            maven {
                url = uri(stagingDir)
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
