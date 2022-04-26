import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    // implementation("org.kodein.di:kodein-di:7.8.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.11.0")

    // Stay @1.6.8 as long as kodein has not updated their packages for new ktor
    val ktorVersion = "1.6.8" // ""2.0.0"
    val exposedVersion = "0.38.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    // implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    //implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    //implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    //implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    // implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    // implementation("io.ktor:ktor-server-websockets:ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.21")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.postgresql:postgresql:42.3.4")
    implementation("org.mindrot:jbcrypt:0.4")
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