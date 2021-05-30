package me.singleneuron.common.util

import me.singleneuron.common.data.EXIFStrings
import java.io.File

expect fun readExif(file: File): EXIFStrings