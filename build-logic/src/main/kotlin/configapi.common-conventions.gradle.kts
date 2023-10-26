import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    compileOnlyApi(libs.annotations)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

val javaVersion = JavaVersion.VERSION_17
val charset = Charsets.UTF_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.ordinal + 1))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

tasks {
    compileJava {
        options.encoding = charset.name()
        options.release.set(javaVersion.ordinal + 1)
    }

    processResources {
        filteringCharset = charset.name()
    }

    test {
        useJUnitPlatform()

        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        }
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
                name = "mavenCentral"

                url = if (version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                }
                credentials(PasswordCredentials::class)

            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
