plugins {
    kotlin("multiplatform")
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.singleneuron.phototimefix"
version = "1.0"

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    js(IR) {
        binaries.executable()
        nodejs {

        }
    }
    mingwX86 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    linuxX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val desktopMain by getting
        val jsMain by getting
        val mingwX86Main by getting
        val linuxX64Main by getting
    }
}

application {
    mainClassName = "MainKt"
}