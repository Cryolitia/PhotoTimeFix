import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "me.singleneuron.phototimefix"
version = "1.0.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Version.java.majorVersion
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.AppImage)
            packageName = "Photo Time Fix"
            description = "An easy program aimed to fix photo's timestamp incorrect by filename/Exif"
            copyright = "Copyright 2018-2022 singleNeuron"
            vendor = "Cryolitia@Github.com"
            licenseFile.set(project.file("../../LICENSE"))
            windows {
                iconFile.set(project.file("icon.ico"))
            }
        }
    }
}

java {
    sourceCompatibility = Version.java
    targetCompatibility = Version.java
}