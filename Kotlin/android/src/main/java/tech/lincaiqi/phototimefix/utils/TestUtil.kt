package tech.lincaiqi.phototimefix.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.lincaiqi.phototimefix.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TestUtil(val context: Context) {

    lateinit var textView: TextView

    suspend fun test() {
        textView = TextView(context)
        textView.setBackgroundColor(Color.parseColor("#efefef"))
        textView.text = context.getString(R.string.runningTest)
        textView.setMargins()
        val frameLayout = FrameLayout(context)
        frameLayout.addView(textView)
        val ad = MaterialAlertDialogBuilder(context).setTitle(R.string.test).setView(frameLayout).setCancelable(false)
            .setPositiveButton(R.string.close, null).show()
        runTest(ad)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @SuppressLint("SetTextI18n")
    private suspend fun runTest(ad: AlertDialog) {
        val button: Button
        withContext(Dispatchers.Main) {
            button = ad.getButton(AlertDialog.BUTTON_POSITIVE)
            button.isEnabled = false
        }
        withContext(Dispatchers.IO) {
            printTest(R.string.creatingFile)
            val file = File(Environment.getExternalStorageDirectory().path, "CompatibilityTestFile")
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                printTest(context.getString(R.string.fault) + e)
                return@withContext
            }
            printTest(R.string.success)
            printTest(R.string.startMode1)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetTime = sdf.parse("2004-09-04 05:21:00")!!.time
            if (file.setLastModified(targetTime)) {
                printTest(R.string.checkResult)
                val dateResult: String = sdf.format(Date(file.lastModified()))
                printTest(dateResult)
                if (dateResult == "2004-09-04 05:21:00") printTest(R.string.checkMode1Finish)
                else printTest(R.string.returnButCannotUse)
                file.setLastModified(sdf.parse("2004-09-04 05:21:00")!!.time)
            } else printTest(R.string.checkMode1Fault)
            printTest(R.string.startCheckMode2)
            if (Shell.rootAccess()) {
                printTest(R.string.checkTouch)
                val result: Shell.Result = Shell.su("touch --help").exec()
                printTest(result.out.toString())
                if (result.isSuccess) {
                    printTest(R.string.findTouch)
                    val result2: Shell.Result = Shell.su("touch /sdcard/CompatibilityTestFile -t 200409040521").exec()
                    printTest(result2.out.toString())
                    if (result2.isSuccess) {
                        printTest(R.string.checkResult)
                        val dateResult: String = sdf.format(Date(file.lastModified()))
                        printTest(dateResult)
                        if (dateResult == "2004-09-04 05:21:00") printTest(R.string.checkMode2Finish)
                        else printTest(R.string.returnButCannotUse)
                    } else printTest(R.string.checkMode2Fault)
                } else printTest(R.string.cannotFindTouch)
            } else printTest(R.string.getRootFault)
            printTest(R.string.cleaning)
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                printTest(context.getString(R.string.fault) + e)
                return@withContext
            }
            printTest(R.string.finish)
        }
        withContext(Dispatchers.Main) {
            ad.setCancelable(true)
            button.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun printTest(string: String) {
        withContext(Dispatchers.Main) {
            textView.text = textView.text.toString() + string
        }
    }

    private suspend fun println() {
        printTest("\n")
    }

    private suspend fun printTest(int: Int) {
        val string = context.getString(int)
        var newString = ""
        for ((index, char) in string.withIndex()) {
            if (char == '\\' && string[index + 1] == 'n') {
                printTest(newString)
                newString = ""
                println()
            } else {
                if (!(char == 'n' && newString == "")) newString += char
            }
        }
        printTest(newString)
    }
}