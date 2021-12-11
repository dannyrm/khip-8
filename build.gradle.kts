group = "uk.co.dmatthews.khip8"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("io.insert-koin:koin-core:3.1.4")
    implementation("io.insert-koin:koin-ktor:3.1.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("io.insert-koin:koin-test-junit5:3.1.4")
    testImplementation("io.strikt:strikt-core:0.33.0")
}

plugins {
    kotlin("jvm") version "1.6.10-RC"
}

repositories {
    mavenCentral()
}

tasks.wrapper {
    gradleVersion = "7.3.1"
}

tasks.build {
    dependsOn(fatJar)
}

tasks.compileKotlin {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    useJUnitPlatform()
    // Removes "Sharing is only supported for boot loader classes because bootstrap classpath has been appended" warning
    jvmArgs = listOf("-Xshare:off")
}

val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "uk.co.dmatthews.khip8.Khip8Bootstrap"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}
