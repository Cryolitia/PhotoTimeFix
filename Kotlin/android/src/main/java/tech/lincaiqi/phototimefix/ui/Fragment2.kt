package tech.lincaiqi.phototimefix.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.singleneuron.common.util.FileStatus.*
import me.singleneuron.common.util.isFileAvailable
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

    private var nowUri: Uri? = null

    private lateinit var binding: Fragment2Binding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, parent, savedInstanceState)
        binding = Fragment2Binding.inflate(inflater, parent, false)
        locateTv = binding.locateText
        preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        choseDateEdit = binding.choseDateEdit
        editor.apply()
        binding.freshButton.setOnClickListener {
            val path: String = locateTv.text.toString()
            freshMedia(path, requireContext())
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            binding.locateText.isClickable = false
        } else {
            binding.locateText.isClickable = true
            binding.locateText.setOnClickListener {
                val textInputLayout = TextInputLayout(requireContext(), null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox)
                val textInputEditText = TextInputEditText(textInputLayout.context)
                textInputLayout.addView(textInputEditText)
                textInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

                val dialog = MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.selectFile)
                    .setView(textInputLayout).setPositiveButton(R.string.OK) { _, _ ->
                        val string = textInputEditText.editableText.toString()
                        if (textInputLayout.error == null) {
                            val file = File(string)
                            nowUri = Uri.fromFile(file)
                            locateTv.setText(nowUri.toString())
                            freshUI()
                        }
                    }.setNegativeButton(R.string.cancel, null).show()
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.isEnabled = false
                textInputEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val fileStatus = isFileAvailable(s.toString())
                        textInputLayout.error = when (fileStatus) {
                            FILE_NOT_EXIST -> {
                                positiveButton.isEnabled = false
                                getString(R.string.fileNotExist)
                            }
                            FILE_NOT_READABLE -> {
                                positiveButton.isEnabled = false
                                getString(R.string.fileNotReadable)
                            }
                            FILE_DIR -> {
                                positiveButton.isEnabled = false
                                getString(R.string.notAFile)
                            }
                            FILE -> {
                                positiveButton.isEnabled = true
                                null
                            }
                        }
                    }
                })
            }
        }
        val dateBtn = binding.dateButton
        dateBtn.setOnClickListener {
            TimeZone.setDefault(TimeZone.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            val selectCalender: Calendar = Calendar.getInstance()
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY)).setMinute(calendar.get(Calendar.MINUTE)).build()
            timePicker.apply {
                addOnPositiveButtonClickListener {
                    selectCalender.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    selectCalender.set(Calendar.MINUTE, timePicker.minute)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                    val date: String = sdf.format(selectCalender.time)
                    val editFormat2: EditText = binding.choseDateEdit
                    editFormat2.setText(date)
                }
            }
            MaterialDatePicker.Builder.datePicker().setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
                .apply {
                    addOnPositiveButtonClickListener {
                        selectCalender.timeInMillis = it
                        timePicker.show(this@Fragment2.requireFragmentManager(), "timePicker")
                    }
                    show(this@Fragment2.requireFragmentManager(), "dataPicker")
                }
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
            requireActivity().toast(R.string.operation_canceled)
        }
        val uri = data?.data
        if (uri != null) {
            nowUri = uri
            locateTv.setText(uri.path)
            freshUI()
        } else {
            requireContext().longToast(getString(R.string.selectError))
        }
    }

    override fun onResume() {
        super.onResume()
        val context = context
        if (context != null) {
            initFragment(preferences, editor, binding.radioGroup)
            if (!bitmapIsNull) updateAppbar(requireActivity(), false)
        }
    }

    private fun freshUI() {
        CoroutineScope(Dispatchers.Main).launch {
            updateImage(nowUri, requireActivity())
        }
        if (nowUri != null) {
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