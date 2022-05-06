import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev679"

}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    android("android")
    jvm("desktop")
    js(IR) {
        browser()
        binaries.executable()
    }
    /*ios {
        binaries {
            framework {
                baseName = "budget-binder-common"
            }
        }
    }*/

    sourceSets {
        val ktorVersion = "2.0.1"
        val commonMain by getting {
            dependencies {
                implementation(project(":budget-binder-common"))

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.kodein.di:kodein-di-framework-compose:7.11.0")
                implementation("org.kodein.di:kodein-di:7.11.0")
            }
        }

        val jvmMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-java:$ktorVersion")
                implementation(compose.foundation)
                implementation(compose.material)
                api(compose.preview)
            }
        }

        val desktopMain by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.desktop.currentOs)
            }
        }

        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)

                implementation("androidx.appcompat:appcompat:1.4.1")
                implementation("androidx.core:core-ktx:1.7.0")

                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
                implementation("androidx.activity:activity-compose:1.4.0")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation(compose.web.svg)
            }
        }

        //val iosMain by getting
    }
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "de.hsfl.budgetBinder.android"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

compose.desktop {
    application {
        mainClass = "de.hsfl.budgetBinder.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            includeAllModules = true
            packageName = "Budget Binder"
            description = "Budget Binder"
            version = "1.0-SNAPSHOT"
            packageVersion = "1.0.0"
        }
    }
}

afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
    }
}