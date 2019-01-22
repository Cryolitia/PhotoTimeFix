package photoTimeFix

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import org.jetbrains.anko.longToast
import tech.lincaiqi.PhotoTimeFix.Core
import tech.lincaiqi.PhotoTimeFix.R
import java.lang.Exception

class Fragment1 : Fragment() {

    private lateinit var editor : SharedPreferences.Editor
    private var core = Core()
    private lateinit var coreK: CoreK
    private lateinit var locateTv : EditText

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_1,parent,false)
        coreK = CoreK(context!!)
        val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        locateTv = view!!.findViewById(R.id.locateText)
        if (preferences.getBoolean("ifFirst",true)) {
            coreK.showAbout()
            editor.putBoolean("ifFirst",false)
            editor.apply()
        }
        coreK.initFragment(view,preferences,editor)

        val choseBtn = view.findViewById<Button>(R.id.chooseButton)
        choseBtn.setOnClickListener {
            context!!.longToast( "由于系统限制(其实是我懒)，请选择文件夹内任意一张图片")
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent,0)
        }

        val startBtn = view.findViewById<Button>(R.id.startButton)
        val startNum : Int = Integer.valueOf(view.findViewById<EditText>(R.id.start).text.toString())
        val endNum : Int = Integer.valueOf(view.findViewById<EditText>(R.id.end).text.toString())
        val fileString : String = view.findViewById<EditText>(R.id.locateText).text.toString()
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val editFormat = view.findViewById<EditText>(R.id.editFormat)
        val format : String = if (editFormat.text.toString().equals(""))  "yyyyMMddHHmm" else editFormat.text.toString()
        startBtn.setOnClickListener {
            core.process(context, startNum, endNum, fileString, editor, radioGroup, R.id.radioButton, R.id.radioButton2, activity, format)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == 0) {
                val originalUri = data!!.data
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                if (originalUri != null) {
                    cursor = context!!.contentResolver.query(originalUri, proj, null, null, null)
                }
                if (cursor != null) {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    var path = cursor.getString(columnIndex)
                    path = path.substring(0, path.lastIndexOf("/"))
                    locateTv.setText(path)
                    cursor.close()
                    editor.putString("locate", path)
                    editor.apply()
                }
            }
        super.onActivityResult(requestCode, resultCode, data)
    } catch (e : Exception) {
            e.printStackTrace()
            context!!.longToast("选择出错，请手动填写路径并联系开发者")
        }
    }

}
