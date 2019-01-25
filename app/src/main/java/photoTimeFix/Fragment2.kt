package photoTimeFix

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import tech.lincaiqi.PhotoTimeFix.R
import java.io.File
import java.util.*
import java.text.SimpleDateFormat


class Fragment2 : Fragment() {

    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var coreK: CoreK
    private lateinit var locateTv : EditText
    private lateinit var chooseBtn : Button
    private lateinit var radioGroup : RadioGroup
    private lateinit var dateEdit :EditText

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, parent, savedInstanceState)
        val view : View = inflater.inflate(R.layout.fragment_2,parent,false)
        locateTv = view.findViewById(R.id.locateText)
        coreK = CoreK(context!!)
        preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        chooseBtn = view.findViewById(R.id.chooseButton)
        radioGroup = view.findViewById(R.id.radioGroup)
        dateEdit = view.findViewById(R.id.nowDate)
        editor.apply()
        locateTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val path:String = locateTv.text.toString()
                dateEdit.setText(coreK.updateDate(path,activity!!))
            }

        })

        val freshBtn = view.findViewById<Button>(R.id.freshButton)
        freshBtn.setOnClickListener {
            val path : String = locateTv.text.toString()
            val file = File(path)
            if (file.exists())
                MediaScannerConnection.scanFile(context, arrayOf(path),null) { _, _ -> activity!!.runOnUiThread { context!!.toast("完成") }}
            else context!!.toast("文件不存在")
        }

        val dateBtn = view.findViewById<Button>(tech.lincaiqi.PhotoTimeFix.R.id.dateButton)
        dateBtn.setOnClickListener {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
            val calendar : Calendar = Calendar.getInstance()
            val selectCalender : Calendar = Calendar.getInstance()
            var nYear : Int = calendar.get(Calendar.YEAR)
            var nMonth : Int = calendar.get(Calendar.MONTH)
            var nDay : Int = calendar.get(Calendar.DAY_OF_MONTH)
            val tp = TimePickerDialog (context, { _, hour: Int, minute: Int ->
                selectCalender.set(nYear,nMonth,nDay,hour,minute)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                val date : String = sdf.format(selectCalender.time)
                val editFormat2 : EditText = view.findViewById(R.id.choseDateEdit)
                editFormat2.setText(date)
            }, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true)
            val dp = DatePickerDialog (context!!, { _, year: Int, month: Int, day: Int ->
                nYear = year
                nMonth = month
                nDay = day
                tp.show()
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            dp.show()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var path = coreK.resultSolve(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (path != "error") {
            locateTv.setText(path)
            dateEdit.setText(coreK.updateDate(path,activity!!))
            path = path.substring(0, path.lastIndexOf("/"))
            editor.putString("locate", path)
            editor.apply()
        } else context!!.longToast("选择出错，请手动填写路径并联系开发者")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (context != null && isVisibleToUser) {
            coreK.initFragment(preferences, editor, chooseBtn, radioGroup, this)
            val iV = activity!!.findViewById<ImageView>(R.id.user_bg)
            if (iV.drawable != null) coreK.updateAppbar(activity!!,false)
        }
    }

}
