buildscript {
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.2.0-beta02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
        classpath("com.android.tools.build:gradle:7.2.2")
    }
}

group = "me.singleneuron.phototimefix"
version = "1.0"

allprojects {
    afterEvaluate {
        // Remove log pollution until Android support in KMP improves.
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.let { kmpExt ->
            kmpExt.sourceSets.removeAll { arrayOf("androidAndroidTestRelease").contains(it.name) }
        }
    }
}