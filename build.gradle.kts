import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "uk.co.dmatthews.khip8"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("ch.qos.logback:logback-classic:1.2.8")
    implementation("io.insert-koin:koin-core:3.1.4")
    implementation("io.insert-koin:koin-ktor:3.1.4")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("io.insert-koin:koin-test-junit5:3.1.4")
    testImplementation("io.strikt:strikt-core:0.33.0")
}

plugins {
    kotlin("jvm") version "1.6.10-RC"
    id("name.remal.sonarlint") version "1.0.189"
    id("jacoco")
}

repositories {
    mavenCentral()
}

tasks.wrapper {
    gradleVersion = "7.3.1"
}

val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "uk.co.dmatthews.khip8.Khip8Bootstrap"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks.build {
    dependsOn(fatJar)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.test {
    useJUnitPlatform()
    // Removes "Sharing is only supported for boot loader classes because bootstrap classpath has been appended" warning
    jvmArgs = listOf("-Xshare:off")

    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.sonarlintMain {
    excludedMessages.add("kotlin:S1135") // Do not alert for TODOs
}
