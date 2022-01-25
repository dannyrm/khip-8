import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.6.10"
    id("io.kotest.multiplatform") version "5.0.2"
}

repositories { mavenCentral() }

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.klogger:klogger:${property("kLogger.version")}")

                implementation("io.insert-koin:koin-core:${property("koin.version")}")
                implementation("io.insert-koin:koin-ktor:${property("koin.version")}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.mockk:mockk:${property("mockK.version")}")
                implementation("io.mockk:mockk-common:${property("mockK.version")}")

                implementation("io.kotest:kotest-assertions-core:${property("kotest.version")}")
                implementation("io.kotest:kotest-framework-engine:${property("kotest.version")}")
                implementation("io.kotest:kotest-framework-datatest:${property("kotest.version")}")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.sksamuel.hoplite:hoplite-core:${property("hoplite.version")}")
                implementation("com.sksamuel.hoplite:hoplite-json:${property("hoplite.version")}")
                implementation("com.soywiz.korlibs.klogger:klogger-jvm:${property("kLogger.version")}")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("io.strikt:strikt-core:${property("strikt.version")}")

                implementation("io.kotest:kotest-runner-junit5-jvm:${property("kotest.version")}")
                implementation("io.kotest:kotest-framework-engine-jvm:${property("kotest.version")}")

                implementation("io.insert-koin:koin-test:${property("koin.version")}") {
                    exclude("org.jetbrains.kotlin", "kotlin-test-junit")
                }
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

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Test> {
        named("jvmTest")

        // Removes "Sharing is only supported for boot loader classes because bootstrap classpath has been appended" warning
        jvmArgs = listOf("-Xshare:off")
    }
}
