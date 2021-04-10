package tech.lincaiqi.phototimefix.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.topjohnwu.superuser.Shell
import tech.lincaiqi.phototimefix.Core
import tech.lincaiqi.phototimefix.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun getTimeByFileName(name: String): Date? {
    try {
        val time = Pattern.compile("[^0-9]").matcher(name).replaceAll("").trim()
        if (time.contains("20") && time.substring(time.indexOf("20")).length >= 14) {
            val targetTime = time.substring(time.indexOf("20"), time.indexOf("20") + 14)
            return SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).parse(targetTime)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun setTimeStampByJava(date: Date, file: File): Throwable? {
    return try {
        if (file.setLastModified(date.time)) {
            Log.d("Succeed", Date(file.lastModified()).toString())
            null
        } else {
            Log.d("Fail", date.toString())
            UnsupportedOperationException("Jvm reported fails when setting ${file.name} to $date")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        e
    }
}

fun setTimeStampByTouch(date: Date, file: File): Throwable? {
    return try {
        val timeString = SimpleDateFormat("yyyyMMddHHmm:ss", Locale.getDefault()).format(date)
        val command = "touch -t " + timeString + " " + file.absolutePath
        Log.d("command", command)
        val result = Shell.su(command).exec()
        if (!result.isSuccess) {
            Log.e("touch", result.out.toString())
            UnsupportedOperationException("Shell reported fails when setting ${file.name} to $date")
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        e
    }
}

suspend fun processDir(file: File, context: Context) {
    try {
        if (!file.exists()) {
            context.toast(R.string.fileNotExistence)
            return
        }
        if (!file.isDirectory) {
            context.toast(R.string.notDictionary)
            return
        }
        processFileList(file.listFiles()!!, context)
    } catch (e:Exception) {
        e.printStackTrace()
        showErrorDialog(e.toString(), context)
    }
}

suspend fun processFileList(files :Array<File>, context: Context) {
    
}