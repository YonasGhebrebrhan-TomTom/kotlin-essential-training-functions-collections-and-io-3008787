plugins {
    kotlin("jvm") version "1.9.25"
    application
}

group = "me.linkedin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    // Single source of truth for the JDK, used by both local builds and CI.
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
