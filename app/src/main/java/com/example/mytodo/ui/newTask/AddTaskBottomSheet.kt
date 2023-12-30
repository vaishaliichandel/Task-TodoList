package com.example.mytodo.ui.newTask

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.BottomShitCreateTaskBinding
import com.example.mytodo.model.Category
import com.example.mytodo.model.SubTaskModel
import com.example.mytodo.model.TaskModel
import com.example.mytodo.ui.category.CategoryNameAdapter
import com.example.mytodo.ui.taskLists.MainViewModel
import com.example.mytodo.utils.CategoryListener
import com.example.mytodo.utils.DataInsertionListener
import com.example.mytodo.utils.MyReceiver
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import com.example.mytodo.viewModelFectory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AddTaskBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding1: BottomShitCreateTaskBinding
    private lateinit var viewModel: MainViewModel
    private var categoryList = ArrayList<Category>()
    private lateinit var taskModel: TaskModel
    private val subTaskList = ArrayList<SubTaskModel>()
    private val attachments = ArrayList<String>()
    private var dataInsertionListener: DataInsertionListener? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding1 = BottomShitCreateTaskBinding.inflate(inflater, container, false)
        if (arguments?.containsKey("taskData") == true) {
            taskModel = (arguments?.getSerializable("taskData") as TaskModel?)!!
            binding1.run {
                createTask.text = getString(R.string.update_task)
                etTask.setText(taskModel.task)
                mbStartDate.text = taskModel.startDate
                mbEndDate.text = taskModel.endDate
                icAddDateTime.text = taskModel.notifyTime
                icAddStar.isChecked = taskModel.isFavorite == 1
                icPin.isChecked = taskModel.isPined == 1
            }
        }
        return binding1.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = TaskRepository(TaskDatabase.getInstance(requireContext()).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        if (viewModel.getAllCate().isEmpty()) {
            categoryList.add(Category(name = "Home", color = R.color.colorHome))
            categoryList.add(Category(name = "Personal", color = R.color.colorPersonal))
            categoryList.add(Category(name = "School", color = R.color.colorSchool))
            categoryList.add(Category(name = "Work", color = R.color.colorWork))
        }
        categoryList.addAll(viewModel.getAllCate().filter { it.isHide == 0 })
        val adapter = CategoryNameAdapter(
            requireContext(),
            R.layout.layout_edittext, R.id.tvName, categoryList
        )
        binding1.run {
            tvCategory.setAdapter(adapter)
            adapter.clickListener(object : CategoryListener {
                override fun clickListener(category: Category, adapterPosition: Int, view: View) {
                    binding1.tvCategory.setText(category.name)
                }
                override fun clickItemListener(
                    category: Category,
                    adapterPosition: Int,
                    view: View
                ) {}
            })

            mbStartDate.setOnClickListener {
                openStartDateDialog(binding1.mbStartDate)
            }
            mbEndDate.setOnClickListener {
                openStartDateDialog(binding1.mbEndDate)
            }
            icAddDateTime.setOnClickListener {
                openNotifyTaskDialog(binding1)
            }
            icCopyTask.setOnClickListener {
                copyTask(binding1)
            }
            icPasteTask.setOnClickListener {
                pasteTask(binding1)
            }
            icAddSubTask.setOnClickListener {
                outlinedTextField2.visible()
                icClose.visible()
            }
            icClose.setOnClickListener {
                outlinedTextField2.gone()
                icClose.gone()
            }
            createTask.setOnClickListener {
                if (binding1.etTask.text?.isEmpty() == true) {
                    binding1.etTask.error = "Please enter task"
                } else {
                    createTask(binding1)
                    dismiss()
                }
            }
        }
    }

    fun setListener(listener: DataInsertionListener) {
        dataInsertionListener = listener
    }
    private fun onDataInserted(data: TaskModel) {
        dataInsertionListener?.onDataInserted(data)

    }
    private fun insertData(data: TaskModel) {
        onDataInserted(data)
    }

    private fun createTask(binding1: BottomShitCreateTaskBinding) {
        if (arguments?.containsKey("taskData") == true) {
            binding1.run {
                subTaskList.add(SubTaskModel(false, etSubTask.text.toString()))
                viewModel.updateUser(
                    TaskModel(
                        id = taskModel.id,
                        subClass = subTaskList,
                        task = etTask.text.toString(),
                        startDate = mbStartDate.text.toString(),
                        endDate = mbEndDate.text.toString(),
                        category = tvCategory.text.toString(),
                        notifyTime = icAddDateTime.text.toString(),
                        isPined = if (icAddStar.isChecked) 1 else 0,
                        isFavorite = if (icAddStar.isChecked) 1 else 0, attachments = attachments
                    )
                )
            }
        } else {
            binding1.run {
                subTaskList.add(SubTaskModel(false, etSubTask.text.toString()))
                val c = Calendar.getInstance()
                val df = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c.time)
                viewModel.insertUser(
                    TaskModel(
                        task = etTask.text.toString(),
                        startDate = if (mbStartDate.text.isEmpty()) formattedDate else mbStartDate.text.toString(),
                        endDate = mbEndDate.text.toString(),
                        category = tvCategory.text.toString(),
                        notifyTime = icAddDateTime.text.toString(),
                        isPined = if (icAddStar.isChecked) 1 else 0,
                        isFavorite = if (icAddStar.isChecked) 1 else 0,
                        subClass = subTaskList,
                        attachments = attachments
                    )
                )
                if (icAddDateTime.text.isNotEmpty()) {
                    scheduleNotification(icAddDateTime.text.toString(), etTask.text.toString())
                }
                insertData(
                    TaskModel(
                        task = etTask.text.toString(),
                        startDate = if (mbStartDate.text.isEmpty()) formattedDate else mbStartDate.text.toString(),
                        endDate = mbEndDate.text.toString(),
                        category = tvCategory.text.toString(),
                        notifyTime = icAddDateTime.text.toString(),
                        isPined = if (icAddStar.isChecked) 1 else 0,
                        isFavorite = if (icAddStar.isChecked) 1 else 0,
                        subClass = subTaskList,
                        attachments = attachments
                    )
                )
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(dateString: String, taskName: String) {
        val format = SimpleDateFormat("dd MMM yyyy h:mm a", Locale.ENGLISH)
        val date = format.parse(dateString)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.get(Calendar.HOUR_OF_DAY) // Replace with your desired hour
        calendar.get(Calendar.MINUTE) // Replace with your desired minute
        calendar.get(Calendar.SECOND)
        Log.e("calendar", "${calendar.timeInMillis} ${calendar.time}")
        val alarmManager =
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), MyReceiver::class.java)
        intent.action = "MY_NOTIFICATION_MESSAGE"
        intent.putExtra("TASK_NAME", taskName)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun pasteTask(binding1: BottomShitCreateTaskBinding) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val pasteData: String
        if (clipboard?.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            val item = clipboard.primaryClip!!.getItemAt(0)
            pasteData = item.text.toString()
            binding1.etTask.setText(pasteData)
        } else {
            Toast.makeText(requireContext(), "Not text found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyTask(binding1: BottomShitCreateTaskBinding) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip =
            ClipData.newPlainText(binding1.etTask.text.toString(), binding1.etTask.text.toString())
        clipboard.setPrimaryClip(clip)
    }

    private fun openNotifyTaskDialog(binding1: BottomShitCreateTaskBinding) {
        val endDate = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        val materialTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select time")
                .build()
        endDate.addOnPositiveButtonClickListener {
            val timeZoneUTC = TimeZone.getDefault()
            val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
            val simpleFormat = SimpleDateFormat("d MMM yyyy", Locale.US)
            val date = Date(it + offsetFromUTC)
            Log.e("date", "$date")

            materialTimePicker.addOnPositiveButtonClickListener {

                val pickedHour: Int = materialTimePicker.hour
                val pickedMinute: Int = materialTimePicker.minute


                val formattedTime: String = when {
                    pickedHour > 12 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour - 12}:0${materialTimePicker.minute} pm"
                        } else {
                            "${materialTimePicker.hour - 12}:${materialTimePicker.minute} pm"
                        }
                    }

                    pickedHour == 12 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour}:0${materialTimePicker.minute} pm"
                        } else {
                            "${materialTimePicker.hour}:${materialTimePicker.minute} pm"
                        }
                    }

                    pickedHour == 0 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour + 12}:0${materialTimePicker.minute} am"
                        } else {
                            "${materialTimePicker.hour + 12}:${materialTimePicker.minute} am"
                        }
                    }

                    else -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour}:0${materialTimePicker.minute} am"
                        } else {
                            "${materialTimePicker.hour}:${materialTimePicker.minute} am"
                        }
                    }
                }
                binding1.icAddDateTime.text =
                    simpleFormat.format(date).plus(" ").plus(formattedTime)
            }
            materialTimePicker.show(parentFragmentManager, TAG)
        }
        endDate.show(parentFragmentManager, TAG)

    }

    private fun openStartDateDialog(button: MaterialButton) {
        val startDate = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select start date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        startDate.addOnPositiveButtonClickListener {
            val timeZoneUTC = TimeZone.getDefault()
            val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
            val simpleFormat = SimpleDateFormat("d MMM yyyy", Locale.US)
            val date = Date(it + offsetFromUTC)
            button.text = simpleFormat.format(date)
        }
        startDate.show(parentFragmentManager, TAG)
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}