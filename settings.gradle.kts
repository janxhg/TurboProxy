@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "turboproxy"

include("turbo-api")
include("turbo-native")
include("turbo-proxy")
include("turbo-proxy-log4j2-plugin")

// Deprecated: Will be removed in Velocity Polymer
include("deprecated-configurate3")

project(":turbo-api").projectDir = file("api")
project(":turbo-native").projectDir = file("native")
project(":turbo-proxy").projectDir = file("proxy")
project(":turbo-proxy-log4j2-plugin").projectDir = file("proxy/log4j2-plugin")

// Deprecated: Will be removed in Velocity Polymer
project(":deprecated-configurate3").projectDir = file("proxy/deprecated/configurate3")

// Check for and include any test plugins
file("proxy/src/test/resources/plugins").listFiles()?.forEach {
    if (it.isDirectory && File(it, "build.gradle.kts").exists()) {
        val name = "turbo-test-plugin-${it.name}"
        include(name)
        project(":$name").projectDir = it
    }
}

val log4j2ProxyPlugin = ":turbo-proxy-log4j2-plugin"
