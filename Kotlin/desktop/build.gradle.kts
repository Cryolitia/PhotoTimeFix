plugins {
    kotlin("multiplatform")
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.singleneuron.phototimefix"
version = "1.0"

kotlin {

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
            }
        }
    }
}

application {
    mainClassName = "MainKt"
}