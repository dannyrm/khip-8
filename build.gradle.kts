group = "uk.co.dmatthews.khip8"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.6.10-RC"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("io.insert-koin:koin-ktor:3.1.2")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.0.0")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("io.insert-koin:koin-test-junit5:3.1.2")
    testImplementation("io.strikt:strikt-core:0.32.0")
}

tasks.create<Jar>("fatJar") {
    duplicatesStrategy = DuplicatesStrategy.FAIL

    manifest {
        attributes(mapOf("Main-Class" to "uk.co.dmatthews.khip8.Khip8Bootstrap"))
    }

    configurations.runtimeClasspath.get().filter {
        it.name.endsWith(".jar")
    }.forEach { jar ->
        from(zipTree(jar))
    }
}