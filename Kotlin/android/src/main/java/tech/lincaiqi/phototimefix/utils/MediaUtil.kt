package tech.lincaiqi.phototimefix.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import tech.lincaiqi.phototimefix.R
import java.io.File

fun freshMedia(path: String, context: Context) {
    val file = File(path)
    val paths: Array<String?>
    if (!file.exists()) {
        context.toast(context.getString(R.string.fileNotExistence))
        return
    }
    if (file.isFile) {
        paths = arrayOf(path)
    } else {
        val files = file.listFiles()!!
        paths = arrayOfNulls(files.size)
        for ((i, f) in files.withIndex()) {
            paths[i] = f.absolutePath
        }
    }
    Log.d("path", paths.toString())
    MediaScannerConnection.scanFile(context, paths, null) { _, _ -> }
    context.toast(R.string.finish)
}

/*fun newReadDate(name: String): String {
        val date: Array<Char?> = arrayOfNulls(12)
        var point = 0
        var i = -1
        while (i++<name.length && point<=11) {
            val s : Char = name[i]
            when (point) {
                0 -> {
                    if (s == '2') date[point++] = s
                }
                1 -> {
                    if ('0' <= s && s <= Calendar.getInstance().get(Calendar.YEAR).toString()[2]) date[point++] = s
                    else {
                        point--
                        i--
                    }
                }
                in 2..3 -> {
                    if (s in '0'..'9') date[point++] = s
                    else {
                        i=i-point+1
                        point=0
                    }
                }
                4 -> {
                    if (s in '0'..'9') {
                        if ((name[i-1]<'0'||name[i-1]>'9')&&(name[i+1]<'0'||name[i+1]>'9')) {
                            date[point++]='0'
                            date[point++]=s
                        } else if (s=='0' || s=='1') date[point++] = s
                    }
                }
                5 -> {
                    if (s in '0'..'9') {
                        if (name[i-1]=='0') date[point++]=s
                        else if (name[i-1]=='1') {
                            if (s in '1'..'2') date[point++]=s
                            else {
                                point--
                                i--
                            }
                        }
                    } else {
                        point--
                        i--
                    }
                }
                6 -> {
                    if (s in '0'..'9') {
                        if ((name[i-1]<'0'||name[i-1]>'9')&&(name[i+1]<'0'||name[i+1]>'9')) {
                            date[point++]='0'
                            date[point++]=s
                        } else if (s in '0'..'3') date[point++] = s
                    }
                }
                7 -> {
                    if (s in '0'..'9') {
                        if (name[i-1] in '0'..'2') date[point] = s
                        else if (name[i-1] == '3') {
                            if (s in '0'..'1') date[point++] = s
                            else {
                                point--
                                i--
                            }
                        }
                    } else {
                        point--
                        i--
                    }
                }
                8 -> {
                    if (s in '0'..'9') {
                        if ((name[i-1]<'0'||name[i-1]>'9')&&(name[i+1]<'0'||name[i+1]>'9')) {
                            date[point++]='0'
                            date[point++]=s
                        } else if (s in '0'..'2') date[point++] = s
                    }
                }
                9 -> {
                    if (s in '0'..'9') {
                        if (name[i-1] in '0'..'1') date[point] = s
                        else if (name[i-1] == '2') {
                            if (s in '0'..'3') date[point++] = s
                        }
                    } else {
                        point--
                        i--
                    }
                }
                10 -> {
                    if (s in '0'..'9') {
                        if ((name[i-1]<'0'||name[i-1]>'9')&&(name[i+1]<'0'||name[i+1]>'9')) {
                            date[point++]='0'
                            date[point++]=s
                        } else if (s in '0'..'5') date[point++] = s
                    }
                }
                11 -> {
                    if (s in '0'..'9') date[point++] = s
                    else {
                        point--
                        i--
                    }
                }
            }
        }
        return date.joinToString("")
    }*/