plugins {
    kotlin("multiplatform") version "1.6.10" apply false
}

allprojects {
    group = "de.hsfl.budgetBinder"
    version = "1.0-SNAPSHOT"
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    }

    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("com.android.tools.lint:lint:30.1.2")
    }
}