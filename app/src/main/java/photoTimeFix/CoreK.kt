package photoTimeFix

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.RadioGroup
import org.jetbrains.anko.longToast
import tech.lincaiqi.PhotoTimeFix.R

class CoreK(private var context: Context) {

    fun initFragment(view: View, preferences: SharedPreferences, editor: SharedPreferences.Editor) {
        val locateText = view.findViewById<EditText>(R.id.locateText)
        locateText.setText(preferences.getString("locate", Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera"))
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.check(preferences.getInt("mode", R.id.radioButton))
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
            val webview: WebView = view.findViewById(R.id.webview)
            webview.webViewClient = WebViewClient()
            webview.loadUrl("file:///android_asset/about.html")
            webview.settings.javaScriptEnabled = true
            webview.addJavascriptInterface(this, "openGit")
            builder.setView(view)
        } catch (e: Exception) {
            builder.setMessage("加载Webview错误，已停止显示帮助窗口。\n该错误并不影响正常功能运行，且开发者仅在模拟器上遇到过，如果出现此对话框请与开发者联系。")
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

}