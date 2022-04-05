/*plugins {
    // Keep it by version 1.6.10
    //kotlin("multiplatform") version "1.6.10" apply false
}*/

allprojects {
    group = "de.hsfl.budgetBinder"
    version = "1.0-SNAPSHOT"
    repositories {
        google()
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.4")
        // DO NOT UPDATE 'com.android.tools.lint:lint'! Keep it by version 30.0.3
        classpath("com.android.tools.lint:lint:30.0.3")
    }
}