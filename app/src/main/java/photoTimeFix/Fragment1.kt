package photoTimeFix

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import org.jetbrains.anko.longToast
import tech.lincaiqi.PhotoTimeFix.Core
import tech.lincaiqi.PhotoTimeFix.R

class Fragment1 : Fragment() {

    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private var core = Core()
    private lateinit var coreK: CoreK
    private lateinit var locateTv : EditText
    private lateinit var locateText : EditText
    private lateinit var chooseBtn : Button
    private lateinit var radioGroup : RadioGroup

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, parent, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_1,parent,false)
        coreK = CoreK(context!!)
        preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        locateTv = view!!.findViewById(R.id.locateText)
        if (preferences.getBoolean("ifFirst",true)) {
            coreK.showAbout()
            editor.putBoolean("ifFirst",false)
            editor.apply()
        }
        locateText = view.findViewById<EditText>(R.id.locateText)
        locateText.setText(preferences.getString("locate", Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera"))
        chooseBtn = view.findViewById<Button>(R.id.chooseButton)
        radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        coreK.initFragment(preferences,editor,chooseBtn,radioGroup,this)

        val startBtn = view.findViewById<Button>(R.id.startButton)
        startBtn.setOnClickListener {
            val startNum : Int = Integer.valueOf(view.findViewById<EditText>(R.id.start).text.toString())
            val endNum : Int = Integer.valueOf(view.findViewById<EditText>(R.id.end).text.toString())
            val fileString : String = view.findViewById<EditText>(R.id.locateText).text.toString()
            val editFormat = view.findViewById<EditText>(R.id.editFormat)
            val format : String = if (editFormat.text.toString().equals(""))  "yyyyMMddHHmm" else editFormat.text.toString()
            val radio : Boolean = radioGroup.checkedRadioButtonId == R.id.radioButton
            Log.d("radio", radioGroup.checkedRadioButtonId.toString())
            Log.d("radio",R.id.radioButton.toString())
            context!!.longToast( "由于系统限制(其实是我懒)，请选择文件夹内任意一张图片")
            core.process(context, startNum, endNum, fileString, radio, activity, format)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var path = coreK.resultSolve(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (path != "error") {
            path = path.substring(0, path.lastIndexOf("/"))
            locateTv.setText(path)
            editor.putString("locate", path)
            editor.apply()
        } else context!!.longToast("选择出错，请手动填写路径并联系开发者")
    }

}
