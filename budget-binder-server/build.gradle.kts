import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project


plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.hsfl.budgetBinder"
version = "1.0-SNAPSHOT"

application {
    // applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set("de.hsfl.budgetBinder.server.MainKt")

    val isDevelopment = true
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":budget-binder-common"))
    implementation(kotlin("stdlib"))

    // implementation("org.kodein.di:kodein-di:7.8.0")
    // implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.8.0")

    val ktor_version= "2.0.0"
    val kotlin_version= "1.6.21"
    val logback_version= "1.2.11"

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    // implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

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
