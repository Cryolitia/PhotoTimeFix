package tech.lincaiqi.phototimefix.utils

import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private val EXIF_TAGS: Array<String> = arrayOf(ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.TAG_SOFTWARE, ExifInterface.TAG_EXPOSURE_TIME, ExifInterface.TAG_F_NUMBER, ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, ExifInterface.TAG_DATETIME, ExifInterface.TAG_EXPOSURE_BIAS_VALUE, ExifInterface.TAG_METERING_MODE, ExifInterface.TAG_LIGHT_SOURCE, ExifInterface.TAG_FOCAL_LENGTH)

fun readEXIF(path: String): EXIFStrings {
    val exifString = EXIFStrings()
    val exifInterface = ExifInterface(path)
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
        } catch (e: java.lang.Exception) {
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

data class EXIFStrings(
        var dateExist: Boolean = false,
        var strings: Array<String?> = emptyArray(),
        var dateString: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EXIFStrings

        if (dateExist != other.dateExist) return false
        if (!strings.contentEquals(other.strings)) return false
        if (dateString != other.dateString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateExist.hashCode()
        result = 31 * result + strings.contentHashCode()
        result = 31 * result + dateString.hashCode()
        return result
    }
}