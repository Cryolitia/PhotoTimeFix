package tech.lincaiqi.phototimefix.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.webkit.JavascriptInterface
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import tech.lincaiqi.phototimefix.R

@Suppress("unused")
class JavaScriptBridge(private val context: Activity) {

    @JavascriptInterface
    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark)).build()).setShowTitle(true)
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
            context.longToast(context.getString(R.string.notInstallCoolapk))
            e.printStackTrace()
        }
    }

    @JavascriptInterface
    fun updateDelay() {
        val editor = context.getPreferences(Context.MODE_PRIVATE).edit()
        val et = EditText(context)
        et.inputType = InputType.TYPE_CLASS_NUMBER
        var input: Int
        AlertDialog.Builder(context).setTitle(R.string.setDelay).setView(et).setPositiveButton(R.string.OK) { _, _ ->
            input = if (et.text.toString() == "") 0 else Integer.parseInt(et.text.toString())
            editor.putInt("delay", input)
            editor.apply()
        }.show()
    }

}