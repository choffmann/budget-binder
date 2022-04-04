plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.1"
}

repositories {
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm("desktop")

    sourceSets {

        val commonJvm by creating {
            dependencies {
                implementation(project(":budget-binder-common"))
                implementation(compose.foundation)
                implementation(compose.material)
                api(compose.preview)
            }
        }

        val desktopMain by getting {
            dependsOn(commonJvm)
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

repositories {
    mavenCentral()
    google()
}

compose.desktop {
    application {
        mainClass = "de.hsfl.budgetBinder.desktop.MainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}