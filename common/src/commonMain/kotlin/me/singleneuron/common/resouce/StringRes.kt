package me.singleneuron.common.resouce

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase

open class StringRes {
    val appName = "Photo Time Fix"
    val fileSelectCard = "Path"
    val fileSelectCard_placeHolder = "File or folder path"
    val settings_safeMode = "Safe Mode"
    val settings_safeMode_summary = "Prevent time from being set before 1970 or after the current time"
    val settings_preferEXIF = "Prefer EXIF"
    val settings_preferEXIF_summary = "Prevent time from being set before 1970 or after the current time"
    val settings_mediaOnly = "Process only media files"
    val settings_mediaOnly_summary = "Only process files with MIME type \"image/*\" or \"video/*\" when batch processing"
    val settings_openMedia = "Open media"
    val about_author = "Author: "
    val about_cryolitia = "Cryolitia"
    val about_openSource = "Github repo: "
    val about_openSourceLicense = "Open Source License"
    val editDateTime_currentModify = "Current modify time: "
    val editDateTime_customFileProcessor = "Custom file processor"
    val editDateTime_start = "Start"
}

val defaultStringRes by lazy {
    val locale = Locale.current.language.lowercase()
    when{
        locale == "zh-cn"||locale == "zh-CN" -> StringRes_zh_CN()
        locale.startsWith("zh") -> StringRes_zh()
        else -> StringRes()
    }
}