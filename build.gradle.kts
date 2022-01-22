import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.6.10"
    id("name.remal.sonarlint") version "1.5.0"
    id("jacoco")
    id("io.kotest.multiplatform") version "5.0.2"
}

repositories { mavenCentral() }

// Test dependencies
val mockKVersion = "1.12.1"
val kotestVersion = "5.0.2"
val striktVersion = "0.33.0"

// Business logic dependencies
val hopliteVersion = "1.4.16"
val kotlinStdLibraryVersion = "1.6.10"
val koinVersion = "3.1.5"
val kLoggerVersion = "2.2.0"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinStdLibraryVersion")

                implementation("com.soywiz.korlibs.klogger:klogger:$kLoggerVersion")

                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-ktor:$koinVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

//                implementation("io.insert-koin:koin-test:$koinVersion")

                implementation("io.mockk:mockk:$mockKVersion")
                implementation("io.mockk:mockk-common:$mockKVersion")

                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
                implementation("com.sksamuel.hoplite:hoplite-json:$hopliteVersion")
                implementation("com.soywiz.korlibs.klogger:klogger-jvm:$kLoggerVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
//                implementation("io.insert-koin:koin-test-junit5:$koinVersion")

                implementation("io.strikt:strikt-core:$striktVersion")

                implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine-jvm:$kotestVersion")
            }
        }
    }
}

tasks {
    wrapper { gradleVersion = "7.3.1" }

    withType<KotlinCompile> {
        kotlinOptions {
            apiVersion = "1.6"
            languageVersion = "1.6"
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    sonarlint { excludes.messages("kotlin:S1135") } // Do not alert for TODOs

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Test> {
        named("jvmTest")

        // Removes "Sharing is only supported for boot loader classes because bootstrap classpath has been appended" warning
        jvmArgs = listOf("-Xshare:off")
    }
}
