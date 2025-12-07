plugins {
    `java-library`
    id("turbo-checkstyle") apply false
    id("turbo-spotless") apply false
}

subprojects {
    apply<JavaLibraryPlugin>()

    // We do not publish the test plugins
    if (path.startsWith(":turbo-test-plugin")) {
        return@subprojects
    }

    apply(plugin = "turbo-checkstyle")
    apply(plugin = "turbo-spotless")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    dependencies {
        testImplementation(rootProject.libs.junit)
    }

    testing.suites.named<JvmTestSuite>("test") {
        useJUnitJupiter()
        targets.all {
            testTask.configure {
                reports.junitXml.required = true
            }
        }
    }
}
