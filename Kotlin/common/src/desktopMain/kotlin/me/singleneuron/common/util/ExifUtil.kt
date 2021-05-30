package me.singleneuron.common.util

import me.singleneuron.common.data.EXIFStrings
import java.io.File

actual fun readExif(file: File): EXIFStrings {
    return EXIFStrings()
}