plugins {
    id("com.android.application")
    kotlin("android")
}

group = "me.singleneuron.phototimefix"
version = "1.0"

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "tech.lincaiqi.PhotoTimeFix"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 15
        versionName = "3.4.2"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile(("proguard-android-optimize.txt")), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0")
    implementation("androidx.browser:browser:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    val libsuVersion = "2.5.1"
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")
    /* Optional: For including a prebuild busybox */
    implementation("com.github.topjohnwu.libsu:busybox:${libsuVersion}")
    implementation("androidx.versionedparcelable:versionedparcelable:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
}