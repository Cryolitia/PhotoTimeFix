package me.singleneuron.common.util

import java.io.File

fun isFileAvailable(string: String): FileStatus {
    return try {
        val file = File(string)
        if (file.exists()) {
            if (file.canRead()) {
                if (file.isFile) {
                    FileStatus.FILE
                } else FileStatus.FILE_DIR
            } else FileStatus.FILE_NOT_READABLE
        } else FileStatus.FILE_NOT_EXIST
    } catch (e: Exception) {
        e.printStackTrace()
        FileStatus.FILE_NOT_EXIST
    }
}

enum class FileStatus {
    FILE_NOT_EXIST, FILE_NOT_READABLE, FILE_DIR, FILE
}