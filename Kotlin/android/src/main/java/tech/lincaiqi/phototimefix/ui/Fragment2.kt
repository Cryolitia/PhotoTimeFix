package tech.lincaiqi.phototimefix.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.singleneuron.common.util.readExif
import tech.lincaiqi.phototimefix.Core
import tech.lincaiqi.phototimefix.R
import tech.lincaiqi.phototimefix.databinding.Fragment2Binding
import tech.lincaiqi.phototimefix.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Fragment2 : Fragment() {

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var locateTv: EditText
    private var bitmapIsNull: Boolean = true
    private lateinit var choseDateEdit: EditText
    private var core = Core()

    private lateinit var binding: Fragment2Binding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, parent, savedInstanceState)
        binding = Fragment2Binding.inflate(inflater, parent, false)
        locateTv = binding.locateText
        preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        choseDateEdit = binding.choseDateEdit
        editor.apply()
        locateTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val path: String = locateTv.text.toString()
                //val returnValue = updateDate(path, activity!!)
                //freshUI(path)
                //bitmapIsNull = (returnValue[0] == "")
            }

        })
        binding.freshButton.setOnClickListener {
            val path: String = locateTv.text.toString()
            freshMedia(path, context!!)
        }

        val dateBtn = binding.dateButton
        dateBtn.setOnClickListener {
            TimeZone.setDefault(TimeZone.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            val selectCalender: Calendar = Calendar.getInstance()
            var nYear: Int = calendar.get(Calendar.YEAR)
            var nMonth: Int = calendar.get(Calendar.MONTH)
            var nDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val tp = TimePickerDialog(context, { _, hour: Int, minute: Int ->
                selectCalender.set(nYear, nMonth, nDay, hour, minute)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                val date: String = sdf.format(selectCalender.time)
                val editFormat2: EditText = binding.choseDateEdit
                editFormat2.setText(date)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            val dp = DatePickerDialog(context!!, { _, year: Int, month: Int, day: Int ->
                nYear = year
                nMonth = month
                nDay = day
                tp.show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            dp.show()
        }

        binding.startButton.setOnClickListener {
            val fileString: String = binding.locateText.text.toString()
            val radio: Boolean = binding.radioGroup.checkedRadioButtonId == R.id.radioButton
            val selectDate = choseDateEdit.text.toString()
            core.process(context, 0, 0, fileString, radio, activity, "yyyyMMddHHmm", selectDate, 0, false)
        }

        binding.chooseButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, this@Fragment2.hashCode() ushr 16)
        }

        binding.showEXIF.setOnClickListener {
            val returnEXIF = readExif(File(locateTv.text.toString()))
            val builder = AlertDialog.Builder(context)
            builder.setMessage(returnEXIF.strings.joinToString(separator = "\n"))
            if (returnEXIF.dateExist) builder.setPositiveButton(getString(R.string.usingEXIF)) { _, _ ->
                choseDateEdit.setText(returnEXIF.dateString)
            }
            else builder.setPositiveButton(getString(R.string.OK), null)
            builder.setNegativeButton(getString(R.string.cancel), null)
            builder.show()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != (this@Fragment2.hashCode() ushr 16)) {
            return super.onActivityResult(requestCode, resultCode, data)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            requireActivity().toast("Operation canceled")
        }
        val uri = data?.data
        if (uri != null) {
            locateTv.setText(uri.path)
            freshUI(uri)
        } else {
            context!!.longToast(getString(R.string.selectError))
            binding.freshButton.isEnabled = false
            binding.showEXIF.isEnabled = false
            binding.startButton.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        val context = context
        if (context != null) {
            initFragment(preferences, editor, binding.radioGroup)
            //context!!.toast(bitmapIsNull.toString())
            if (!bitmapIsNull) updateAppbar(activity!!, false)
        }
    }

    private fun freshUI(path: Uri?) {
        CoroutineScope(Dispatchers.Main).launch {
            val returnValue = updateImage(path, activity!!)
        }
        if (path != null) {
            binding.freshButton.isEnabled = true
            binding.showEXIF.isEnabled = true
            binding.startButton.isEnabled = true
        } else {
            binding.freshButton.isEnabled = false
            binding.showEXIF.isEnabled = false
            binding.startButton.isEnabled = false
        }
    }

}