plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    `maven-publish`
}

group = "com.niloda.contextdialog"
version = "0.1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.workflow1:workflow-core-jvm:1.22.0")
    implementation("io.arrow-kt:arrow-core:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("Stateful Dialog")
                description.set("A Kotlin library for managing stateful dialog flows")
                url.set("https://github.com/NicholasPowell/stateful-dialog")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("NicholasPowell")
                        name.set("Nicholas Powell")
                        email.set("nikpowell@example.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/NicholasPowell/stateful-dialog.git")
                    developerConnection.set("scm:git:ssh://github.com/NicholasPowell/stateful-dialog.git")
                    url.set("https://github.com/NicholasPowell/stateful-dialog")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/NicholasPowell/stateful-dialog")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
            }
        }
    }
}