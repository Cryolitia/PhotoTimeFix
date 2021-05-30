plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "me.singleneuron.phototimefix.common"
version = "1.0"

repositories {
    google()
}

kotlin {
    android()

    val hostOs = System.getProperty("os.name")
    val isMingw = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> jvm("linux")
        hostOs == "Linux" -> jvm("linux")
        isMingw -> jvm("windows")
        else -> throw GradleException("Host OS is not supported")
    }

    nativeTarget.apply {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("androidx.exifinterface:exifinterface:1.3.2")
            }
        }
        val desktopMain by creating {
            dependsOn(commonMain)
        }

        if (!isMingw) {
            val unixMain by getting {
                dependsOn(desktopMain)
            }
        } else {
            val windowsMain by getting {
                dependsOn(desktopMain)
            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
}
