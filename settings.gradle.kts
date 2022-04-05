pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "budget-binder"

include(":budget-binder-common")
include(":budget-binder-multiplatform-app")