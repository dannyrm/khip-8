import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.soywiz.korge.gradle.*
import kotlinx.kover.KoverPlugin

group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.7.0"
    id("io.kotest.multiplatform") version "5.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
}

repositories { mavenCentral() }

buildscript {
    val korgePluginVersion: String by project

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
    }
}

korge {
    id = "com.github.dannyrm.khip8"
    name = "Khip-8"

    targetJvm()

    jvmMainClassName = "com.github.dannyrm.khip8.display.view.KorgeUiKt"
}

kotlin {
    jvm()

    apply<KorgeGradlePlugin>()
    apply<KoverPlugin>()

    configurations.all {
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.klogger:klogger:${property("kLogger.version")}")
                implementation("io.insert-koin:koin-core:${property("koin.version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlinStdLibrary.version")}")
                implementation("com.russhwolf:multiplatform-settings:0.8.1")
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

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${property("kotlinStdLibrary.version")}")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.klogger:klogger-jvm:${property("kLogger.version")}")
            }

            resources.srcDir("resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation("io.strikt:strikt-core:${property("strikt.version")}")

                implementation("io.kotest:kotest-runner-junit5-jvm:${property("kotest.version")}")
                implementation("io.kotest:kotest-framework-engine-jvm:${property("kotest.version")}")

                implementation("io.insert-koin:koin-test:${property("koin.version")}")
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
