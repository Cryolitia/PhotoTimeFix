package me.singleneuron.common.util


import androidx.exifinterface.media.ExifInterface
import me.singleneuron.common.data.EXIFStrings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val EXIF_TAGS: Array<String> = arrayOf(ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.TAG_SOFTWARE, ExifInterface.TAG_EXPOSURE_TIME, ExifInterface.TAG_F_NUMBER, ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, ExifInterface.TAG_DATETIME, ExifInterface.TAG_EXPOSURE_BIAS_VALUE, ExifInterface.TAG_METERING_MODE, ExifInterface.TAG_LIGHT_SOURCE, ExifInterface.TAG_FOCAL_LENGTH)

actual fun readExif(file: File): EXIFStrings {
    val exifString = EXIFStrings()
    val exifInterface = ExifInterface(file)
    val strings = arrayOfNulls<String>(EXIF_TAGS.size)
    for ((i, tag) in EXIF_TAGS.withIndex()) {
        strings[i] = tag + ":" + exifInterface.getAttribute(tag)
        exifString.strings = strings
    }
    if (exifInterface.getAttribute(ExifInterface.TAG_DATETIME) != null) {
        exifString.dateExist = true
        var sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
        try {
            val d: Date = sdf.parse(exifInterface.getAttribute(ExifInterface.TAG_DATETIME)!!)!!
            sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            exifString.dateString = sdf.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return exifString
}

fun getExitData(file: File): Date? {
    try {
        val exifInterface = ExifInterface(file)
        if (exifInterface.getAttribute(ExifInterface.TAG_DATETIME) != null) {
            val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
            return sdf.parse(exifInterface.getAttribute(ExifInterface.TAG_DATETIME)!!)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}