pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


rootProject.name = "bridges"


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("bridge-api")
include("bridge-gate")
include("bridge-velocity")
include("bridge-fabric")
include("bridge-bungeecord")
include("bridge-fabric:v1_21_8")
include("bridge-fabric:v1_21_5")
include("bridge-waterdog")
include("bridge-fabric:v1_21_10")