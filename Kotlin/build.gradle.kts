import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
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
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
    ktlint("com.pinterest.ktlint:ktlint-cli:1.7.1")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}

application {
    mainClass.set("MainKt")
}
