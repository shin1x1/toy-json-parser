import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "me.shin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
    ktlint("com.pinterest.ktlint:ktlint-cli:1.3.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}

application {
    mainClass.set("MainKt")
}
