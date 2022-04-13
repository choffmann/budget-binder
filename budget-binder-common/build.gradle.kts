plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    linuxX64()
    js(IR) {
        browser()
    }

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}