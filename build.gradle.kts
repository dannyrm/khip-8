import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.dannyrm.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.6.10"
    id("name.remal.sonarlint") version "1.5.0"
    id("jacoco")
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

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
                implementation("io.mockk:mockk:1.12.1")
//                implementation("io.insert-koin:koin-test-junit5:3.1.4")
                implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
                implementation("io.strikt:strikt-core:0.33.0")
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
