dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
        maven {
            url = uri("https://maven.pkg.github.com/Cryolitia/LicensesDialog")
            credentials {
                username = "Cryolitia"
                password = "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"
            }
        }
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "PhotoTimeFix"

include(":android")
include(":desktop")
include(":common")
include(":common-desktop")