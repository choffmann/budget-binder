import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.hsfl.budgetBinder"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("de.hsfl.budgetBinder.server.MainKt")

    val dev = System.getenv("DEV") != null
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$dev")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":budget-binder-common"))
    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-network-tls-certificates-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-forwarded-header:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("org.postgresql:postgresql:42.3.6")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("com.github.ajalt.clikt:clikt:3.5.0")
    implementation("com.sksamuel.hoplite:hoplite-core:2.1.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.1.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.21")
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
