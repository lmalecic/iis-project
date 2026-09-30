plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    kotlin("plugin.compose") version "2.4.20"

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.jakewharton.mosaic:mosaic-runtime:0.18.0")
    implementation("com.jakewharton.mosaic:mosaic-animation:0.18.0")
    implementation(project(":protocol-contracts"))
    implementation("io.grpc:grpc-netty-shaded")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "com.lmalecic.iis.client.ApplicationKt"
}
