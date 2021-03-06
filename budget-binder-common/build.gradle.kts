plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.6.20"
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
    }
}