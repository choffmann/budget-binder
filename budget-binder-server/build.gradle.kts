import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.hsfl.budgetBinder"
version = "1.0-SNAPSHOT"

application {
    // applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set("de.hsfl.budgetBinder.server.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":budget-binder-common"))
    implementation(kotlin("stdlib"))

    // implementation("org.kodein.di:kodein-di:7.8.0")
    // implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.8.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.ktor:ktor-server-core:1.6.4")
    implementation("io.ktor:ktor-server-netty:1.6.4")
    // implementation("io.ktor:ktor-html-builder:1.6.4")
    implementation("io.ktor:ktor-serialization:1.6.4")
    implementation("io.ktor:ktor-auth:1.6.4")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // implementation("org.jetbrains.exposed:exposed-core:0.34.1")
    // implementation("org.jetbrains.exposed:exposed-dao:0.34.1")
    // implementation("org.jetbrains.exposed:exposed-jdbc:0.34.1")
    // implementation("org.xerial:sqlite-jdbc:3.30.1")
    // implementation("org.mindrot:jbcrypt:0.4")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "io.ktor.server.netty.EngineMain"))
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}