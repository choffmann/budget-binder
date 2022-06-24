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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("com.android.tools.build:gradle:7.0.4")
        // Keep 'com.android.tools.lint:lint' @30.0.3
        classpath("com.android.tools.lint:lint:30.0.3")
    }
}
