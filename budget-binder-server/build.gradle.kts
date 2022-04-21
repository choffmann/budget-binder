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

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    // implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.21")

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
