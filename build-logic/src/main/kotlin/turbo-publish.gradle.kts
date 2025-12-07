plugins {
    java
    `maven-publish`
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            credentials(PasswordCredentials::class.java)

            name = if (version.toString().endsWith("SNAPSHOT")) "paperSnapshots" else "paper" // "paper" is seemingly not defined
            val base = "https://artifactory.papermc.io/artifactory"
            val releasesRepoUrl = "$base/releases/"
            val snapshotsRepoUrl = "$base/snapshots/"
            setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("TurboProxy")
                description.set("A high performance, scalable, and secure Minecraft proxy.")
                url.set("https://github.com/PaperMC/TurboProxy") // TODO: Update URL

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("tux")
                        name.set("Andrew Steinborn")
                        email.set("git@tux.io")
                    }
                    developer {
                        id.set("paper")
                        name.set("PaperMC")
                        email.set("info@papermc.io")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/PaperMC/TurboProxy.git")
                    developerConnection.set("scm:git:https://github.com/PaperMC/TurboProxy.git")
                    url.set("https://github.com/PaperMC/TurboProxy")
                }
            }
        }
    }
}
