import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.soywiz.korge.gradle.*

group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.7.0"
    id("io.kotest.multiplatform") version "5.3.0"
}

repositories { mavenCentral() }

buildscript {
    val korgePluginVersion = "2.4.7"

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
    val kotlinStdLibraryVersion="1.6.3"
    val koinVersion="3.2.0"
    val kLoggerVersion="2.2.0"
    val mockKVersion="1.12.4"
    val koTestVersion="5.3.2"
    val striktVersion="0.34.1"
    val multiplatformSettingsVersion="0.8.1"

    jvm()

    apply<KorgeGradlePlugin>()

    configurations.all {
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.klogger:klogger:$kLoggerVersion")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-annotations:1.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinStdLibraryVersion")
                implementation("com.russhwolf:multiplatform-settings:$multiplatformSettingsVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.mockk:mockk:$mockKVersion")
                implementation("io.mockk:mockk-common:$mockKVersion")

                implementation("io.kotest:kotest-assertions-core:$koTestVersion")
                implementation("io.kotest:kotest-framework-engine:$koTestVersion")
                implementation("io.kotest:kotest-framework-datatest:$koTestVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinStdLibraryVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.klogger:klogger-jvm:$kLoggerVersion")
            }

            resources.srcDir("resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation("io.strikt:strikt-core:$striktVersion")

                implementation("io.kotest:kotest-runner-junit5-jvm:$koTestVersion")
                implementation("io.kotest:kotest-framework-engine-jvm:$koTestVersion")

                implementation("io.insert-koin:koin-test:$koinVersion")
            }
        }
    }
}

tasks {
    wrapper { gradleVersion = "7.3.1" }

    withType<KotlinCompile> {
        kotlinOptions {
            apiVersion = "1.7"
            languageVersion = "1.7"
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
