plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose") version "1.1.1"
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
    ios {
        binaries {
            framework {
                baseName = "budget-binder-common"
            }
        }
    }

    sourceSets {
        val jvmMain by creating {
            dependencies {
                implementation(project(":budget-binder-common"))
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

                implementation("io.ktor:ktor-client-android:1.6.4")

                implementation("org.kodein.di:kodein-di:7.9.0")
                implementation("org.kodein.di:kodein-di-framework-android-x:7.9.0")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(project(":budget-binder-common"))
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation(compose.web.svg)
            }
        }

        val iosMain by getting
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
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "jvm"
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