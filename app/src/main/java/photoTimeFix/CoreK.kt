package photoTimeFix

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.AppBarLayout
import android.support.media.ExifInterface
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import tech.lincaiqi.PhotoTimeFix.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class CoreK(private var context: Context, private var editor: SharedPreferences.Editor, private var activity: Activity?) {

    val EXIF_TAGS: Array<String> = arrayOf(ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.TAG_SOFTWARE, ExifInterface.TAG_EXPOSURE_TIME, ExifInterface.TAG_F_NUMBER, ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, ExifInterface.TAG_DATETIME, ExifInterface.TAG_EXPOSURE_BIAS_VALUE, ExifInterface.TAG_METERING_MODE, ExifInterface.TAG_LIGHT_SOURCE, ExifInterface.TAG_FOCAL_LENGTH)

    fun initFragment(preferences: SharedPreferences, editor: SharedPreferences.Editor, chooseBtn: Button, radioGroup: RadioGroup, fragment: Fragment) {
        radioGroup.check(preferences.getInt("mode", R.id.radioButton))
        chooseBtn.setOnClickListener {
            context.longToast("由于系统限制(其实是我懒)，请选择文件夹内任意一张图片")
            chooseFile(fragment)
        }
        radioGroup.setOnCheckedChangeListener { _, i ->
            editor.putInt("mode", i)
            editor.apply()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "InflateParams")
    fun showAbout() {
        val builder = AlertDialog.Builder(context)
        try {
            val view = LayoutInflater.from(context).inflate(R.layout.about, null)
            val webView: WebView = view.findViewById(R.id.webview)
            webView.webViewClient = WebViewClient()
            webView.loadUrl("file:///android_asset/about.html")
            webView.settings.javaScriptEnabled = true
            webView.requestFocusFromTouch()
            webView.addJavascriptInterface(this, "openGit")
            builder.setView(view)
        } catch (e: Exception) {
            builder.setMessage("加载WebView错误，已停止显示帮助窗口。\n该错误并不影响正常功能运行，且开发者仅在模拟器上遇到过，如果出现此对话框请与开发者联系。")
            e.printStackTrace()
        }
        builder.setPositiveButton("确定", null)
        builder.show()
    }

    @JavascriptInterface
    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setShowTitle(true)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    //作者：花生酱啊
    //来源：CSDN
    //原文：https://blog.csdn.net/u011286957/article/details/80824235
    //版权声明：本文为博主原创文章，转载请附上博文链接！
    @JavascriptInterface
    fun openCoolapk(user: String) {
        val intent = Intent()
        try {
            //intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse("coolmarket://u/$user")
            context.startActivity(intent)
        } catch (e: Exception) {
            context.longToast("未安装酷安")
            e.printStackTrace()
        }

    }

    private fun chooseFile(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        fragment.startActivityForResult(intent, 0)
    }

    fun resultSolve(requestCode: Int, data: Intent?): String {
        try {
            return if (requestCode == 0) {
                val originalUri = data!!.data
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                if (originalUri != null) {
                    cursor = context.contentResolver.query(originalUri, proj, null, null, null)
                }
                if (cursor != null) {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    val path = cursor.getString(columnIndex)
                    cursor.close()
                    path
                } else "error"
            } else "error"

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return "error"
        }
    }

    fun updateDate(path: String, activity: Activity): Array<String?> {
        val file = File(path)
        val imageView: ImageView = activity.findViewById(R.id.user_bg)
        val returnArray = arrayOfNulls<String>(2)
        if (file.exists() && file.isFile) {
            val bm: Bitmap = BitmapFactory.decodeFile(path)
            imageView.setImageBitmap(bm)
            updateAppbar(activity, false)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            returnArray[0] = sdf.format(Date(file.lastModified()))
            var time = file.name
            time = Pattern.compile("[^0-9]").matcher(time).replaceAll("").trim { it <= ' ' }
            Log.d("date", time)
            if (time.contains("20") && time.substring(time.indexOf("20")).length >= 12) {
                val targetTime = time.substring(time.indexOf("20"), time.indexOf("20") + 12)
                try {
                    returnArray[1] = sdf.format(SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).parse(targetTime))
                } catch (e: Exception) {
                    returnArray[1] = ""
                    e.printStackTrace()
                }
            } else {
                returnArray[1] = ""
            }
        } else {
            imageView.setImageBitmap(null)
            updateAppbar(activity, true)
            returnArray[0] = ""
            returnArray[1] = ""
        }
        return returnArray
    }

    fun updateAppbar(activity: Activity, scrollAble: Boolean) {
        val mAppBarLayout = activity.findViewById<AppBarLayout>(R.id.app_bars)
        val mAppBarChildAt: View = mAppBarLayout.getChildAt(0)
        val mAppBarParams: AppBarLayout.LayoutParams = mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams
        mAppBarParams.scrollFlags = if (scrollAble) AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        else 0
        mAppBarChildAt.layoutParams = mAppBarParams
        /* 作者：Silas_
           来源：CSDN
           原文：https://blog.csdn.net/qq_31852701/article/details/80859644
           版权声明：本文为博主原创文章，转载请附上博文链接！ */
    }

    fun freshMedia(path: String, context: Context) {
        val file = File(path)
        val paths: Array<String?>
        if (!file.exists()) {
            context.toast("文件或目录不存在")
            return
        }
        if (file.isFile) {
            paths = arrayOf(path)
        } else {
            val files = file.listFiles()
            paths = arrayOfNulls(files.size)
            for ((i, f) in files.withIndex()) {
                paths[i] = f.absolutePath
            }
        }
        Log.d("path", paths.toString())
        MediaScannerConnection.scanFile(context, paths, null) { _, _ -> }
        context.toast("完成")
    }

    @JavascriptInterface
    fun updateDelay() {
        val et = EditText(context)
        et.inputType = InputType.TYPE_CLASS_NUMBER
        var input: Int
        AlertDialog.Builder(context).setTitle("设置延时")
                .setView(et)
                .setPositiveButton("确定") { _, _ ->
                    input = et.text.toString().toInt()
                    editor.putInt("delay", input)
                    editor.apply()
                }
                .show()
    }

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
                val d: Date = sdf.parse(exifInterface.getAttribute(ExifInterface.TAG_DATETIME))
                sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                exifString.dateString = sdf.format(d)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return exifString
    }

    class EXIFStrings {
        var dateExist: Boolean = false
        var strings: Array<String?> = emptyArray()
        var dateString: String = ""
    }

    @JavascriptInterface
    fun switchIcon() {
        val defaultComponentName = ComponentName(activity!!.baseContext,"photoTimeFix.MainActivity")
        val newComponentName = ComponentName(activity!!.baseContext,"photoTimeFix.newIcon")
        val packageManager = activity!!.packageManager
        AlertDialog.Builder(context).setTitle("切换图标")
                .setMessage("确定以使用新版，取消使用旧版")
                .setPositiveButton("确定") { _, _ ->
                    packageManager.setComponentEnabledSetting(defaultComponentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(newComponentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
                }
                .setNegativeButton("取消") {_, _ ->
                    packageManager.setComponentEnabledSetting(newComponentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
                    packageManager.setComponentEnabledSetting(defaultComponentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
                }
                .show()
    }

    fun newReadDate(name: String): String {
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
    }

}