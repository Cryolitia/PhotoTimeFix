buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
        classpath("com.android.tools.build:gradle:4.1.1")
    }
}

group = "me.singleneuron"
version = "1.0"

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            setUrl("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            setUrl("https://jitpack.io")
        }
    }
}