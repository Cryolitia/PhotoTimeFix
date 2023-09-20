import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("org.jetbrains.compose")
    kotlin("multiplatform")
    id("com.android.library")
}

group = "me.singleneuron.phototimefix"
version = "1.0"

kotlin {
    targets {
        android()
        jvm()
    }

    val hostOs = System.getProperty("os.name")
    val isMingw = hostOs.startsWith("Windows")

    android{
        compilations.all {
            kotlinOptions.jvmTarget = Version.java.majorVersion
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                api(compose.preview)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)
                api(kotlin("stdlib-common"))
                api(kotlin("stdlib-jdk8"))
                api("io.github.cryolitia.licensesdialog.compose:common:1.0.2")
                api("org.apache-extras.beanshell:bsh:2.0b6")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.activity:activity-compose:1.6.0")
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
                api("com.google.android.material:material:1.6.1")
                api("androidx.exifinterface:exifinterface:1.3.3")
                api("androidx.compose.material3:material3:1.0.0-beta03")
                api("androidx.compose.material:material:1.3.0-beta03")
                api("com.google.accompanist:accompanist-flowlayout:0.23.1")
            }
        }
        val unixMain by creating {
            dependsOn(commonMain)
        }

        val windowsMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            if (!isMingw) {
                dependsOn(unixMain)
            } else {
                dependsOn(windowsMain)
            }
            dependencies {
                api(compose.desktop.common)
                api("com.drewnoakes:metadata-extractor:2.18.0")
                api("com.github.lgooddatepicker:LGoodDatePicker:11.2.1")
            }
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility(Version.java)
        targetCompatibility(Version.java)
    }
    (this as ExtensionAware).configure<KotlinJvmOptions> {
        jvmTarget = Version.java.majorVersion
        freeCompilerArgs += "-Xjvm-default=all"
    }
    namespace = "me.singleneuron.phototimefix.common"
}

java {
    sourceCompatibility = Version.java
    targetCompatibility = Version.java
}