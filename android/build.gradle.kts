plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

group = "me.singleneuron.phototimefix"
version = "1.0"

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "tech.lincaiqi.PhotoTimeFix"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = Version.java
        targetCompatibility = Version.java
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    kotlinOptions{
        jvmTarget = Version.java.majorVersion
    }
    namespace = "tech.lincaiqi.phototimefix"
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.browser:browser:1.4.0")
    val libsuVersion = "5.0.2"
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
}
repositories {
    mavenCentral()
}
