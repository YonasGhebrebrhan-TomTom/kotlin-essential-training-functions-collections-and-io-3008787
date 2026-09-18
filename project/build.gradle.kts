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
    // Every lesson is its own file with its own main(), so there is no single entry point.
    // Pick one:  ./gradlew run -Plesson=ch02.StringsKt
    // The default names the course's first lesson file.
    mainClass.set(providers.gradleProperty("lesson").orElse("ch02.BooleansKt"))
}
