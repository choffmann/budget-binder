plugins {
    kotlin("multiplatform")
}

group = "de.hsfl.budgetBinder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    linuxX64()
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
            }
        }
    }
}