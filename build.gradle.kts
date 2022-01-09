group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.6.10"
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")

                implementation("com.soywiz.korlibs.klogger:klogger:2.2.0")
                implementation("com.soywiz.korlibs.klogger:klogger-jvm:2.2.0")

                implementation("io.insert-koin:koin-core:3.1.4")
                implementation("io.insert-koin:koin-ktor:3.1.4")
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation("com.sksamuel.hoplite:hoplite-core:1.4.16")
                implementation("com.sksamuel.hoplite:hoplite-json:1.4.16")
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
                implementation("io.mockk:mockk:1.12.1")
                implementation("io.insert-koin:koin-test-junit5:3.1.4")
                implementation("io.strikt:strikt-core:0.33.0")
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
