// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Include the `app` and `utils` subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html
include("protocol-contracts")
include(":backend")
include(":client")

includeBuild("mosaic-stream29") {
    dependencySubstitution {
        substitute(module("com.jakewharton.mosaic:mosaic-testing"))
            .using(project(":mosaic-testing"))

        substitute(module("com.jakewharton.mosaic:mosaic-testing-jvm"))
            .using(project(":mosaic-testing"))

        substitute(module("com.jakewharton.mosaic:mosaic-runtime"))
            .using(project(":mosaic-runtime"))

        substitute(module("com.jakewharton.mosaic:mosaic-runtime-jvm"))
            .using(project(":mosaic-runtime"))

        substitute(module("com.jakewharton.mosaic:mosaic-animation"))
            .using(project(":mosaic-animation"))

        substitute(module("com.jakewharton.mosaic:mosaic-animation-jvm"))
            .using(project(":mosaic-animation"))
    }
}

rootProject.name = "iis"

