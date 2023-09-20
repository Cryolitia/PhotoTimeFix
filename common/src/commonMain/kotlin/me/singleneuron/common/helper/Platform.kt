package me.singleneuron.common.helper

enum class Platform {
    Android, Windows, Unix
}

expect fun getPlatform(): Platform