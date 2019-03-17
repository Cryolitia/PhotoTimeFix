package photoTimeFix

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.longToast
import tech.lincaiqi.PhotoTimeFix.Core
import tech.lincaiqi.PhotoTimeFix.R
import java.util.*
import java.text.SimpleDateFormat


class Fragment2 : Fragment() {

    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var coreK : CoreK
    private lateinit var locateTv : EditText
    private lateinit var chooseBtn : Button
    private lateinit var freshBtn : Button
    private lateinit var exifBtn : Button
    private lateinit var startBtn : Button
    private lateinit var radioGroup : RadioGroup
    private lateinit var dateEdit : EditText
    private var bitmapIsNull : Boolean = true
    private lateinit var choseDateEdit : EditText
    private var core = Core()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, parent, savedInstanceState)
        val view : View = inflater.inflate(R.layout.fragment_2,parent,false)
        locateTv = view.findViewById(R.id.locateText)
        preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        coreK = CoreK(context!!,editor,null)
        chooseBtn = view.findViewById(R.id.chooseButton)
        radioGroup = view.findViewById(R.id.radioGroup)
        dateEdit = view.findViewById(R.id.nowDate)
        choseDateEdit = view.findViewById(R.id.choseDateEdit)
        editor.apply()
        locateTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val path:String = locateTv.text.toString()
                val returnValue = coreK.updateDate(path,activity!!)
                if (returnValue[0]!="") {
                    freshBtn.isEnabled = true
                    exifBtn.isEnabled = true
                    startBtn.isEnabled = true
                } else {
                    freshBtn.isEnabled = false
                    exifBtn.isEnabled = false
                    startBtn.isEnabled = false
                }
                dateEdit.setText(returnValue[0])
                choseDateEdit.setText(returnValue[1])
                bitmapIsNull = (returnValue[0]=="")
            }

        })
        exifBtn = view.findViewById(R.id.showEXIF)
        freshBtn = view.findViewById(R.id.freshButton)
        freshBtn.setOnClickListener {
            val path : String = locateTv.text.toString()
            coreK.freshMedia(path,context!!)
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

        startBtn = view.findViewById<Button>(R.id.startButton)
        startBtn.setOnClickListener {
            val fileString: String = view.findViewById<EditText>(R.id.locateText).text.toString()
            val radio: Boolean = radioGroup.checkedRadioButtonId == R.id.radioButton
            val selectDate = choseDateEdit.text.toString()
            core.process(context, 0, 0, fileString, radio, activity, "yyyyMMddHHmm", selectDate,0)
        }

        exifBtn.setOnClickListener {
            val returnEXIF = coreK.readEXIF(locateTv.text.toString())
            val builder = AlertDialog.Builder(context)
            builder.setMessage(returnEXIF.strings.joinToString(separator = "\n"))
            if (returnEXIF.dateExist) builder.setPositiveButton("使用EXIF时间") { _, _ ->
                choseDateEdit.setText(returnEXIF.dateString)
            }
            else builder.setPositiveButton("确定",null)
            builder.setNegativeButton("取消",null)
            builder.show()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var path = coreK.resultSolve(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (path != "error") {
            locateTv.setText(path)
            val returnValue = coreK.updateDate(path,activity!!)
            if (returnValue[0]!="") {
                freshBtn.isEnabled = true
                exifBtn.isEnabled = true
                startBtn.isEnabled = true
            } else {
                freshBtn.isEnabled = false
                exifBtn.isEnabled = false
                startBtn.isEnabled = false
            }
            dateEdit.setText(returnValue[0])
            choseDateEdit.setText(returnValue[1])
            path = path.substring(0, path.lastIndexOf("/"))
            editor.putString("locate", path)
            editor.apply()
        } else {
            context!!.longToast("选择出错，请手动填写路径并联系开发者")
            freshBtn.isEnabled = false
            exifBtn.isEnabled = false
            startBtn.isEnabled = false
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (context != null && isVisibleToUser) {
            coreK.initFragment(preferences, editor, chooseBtn, radioGroup, this)
            //context!!.toast(bitmapIsNull.toString())
            if (!bitmapIsNull) coreK.updateAppbar(activity!!,false)
        }
    }

}
