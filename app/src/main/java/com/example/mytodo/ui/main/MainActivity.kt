package com.example.mytodo.ui.main

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.ActivityMainBinding
import com.example.mytodo.databinding.BottomShitCreateTaskBinding
import com.example.mytodo.model.SubTaskModel
import com.example.mytodo.model.TaskModel
import com.example.mytodo.ui.subTask.SubTaskActivity
import com.example.mytodo.utils.TaskClickListener
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import com.example.mytodo.viewModelFectory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var viewModel: MainViewModel
    private var isFavorite = true
    private val taskList = ArrayList<TaskModel>()
    private val favTaskList = ArrayList<TaskModel>()
    private val modalBottomSheet = AddTaskBottomSheet()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        observer()
        clickListeners()
    }

    private fun observer() {
        viewModel.getAllUsers().observe(this) { task ->
            mainAdapter.addAll(task.toSet())
            favTaskList.addAll(task.toSet())
        }
    }

    private fun clickListeners() {
        binding.run {
            fabAdd.setOnClickListener(this@MainActivity)
            bottomAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.accelerator -> {
                        if (isFavorite) {
                            mainAdapter.addAll(favTaskList.filter { it.isFavorite == 1 }.toSet())
                            item.setIcon(R.drawable.ic_filled_star)
                        } else {
                            mainAdapter.addAll(favTaskList.toSet())
                            item.setIcon(R.drawable.ic_unfilled_star)
                        }
                        isFavorite = !isFavorite
                    }
                }
                true
            }
        }
        mainAdapter.setOnClickListener(object : TaskClickListener {
            override fun onItemClick(position: Int, view: View, model: TaskModel) {
                startActivity(
                    Intent(
                        this@MainActivity,
                        SubTaskActivity::class.java
                    ).putExtra("TASK_DATA", model)
                )

            }

            override fun onItemCheckedListener(
                adapterPosition: Int,
                buttonView: CompoundButton,
                isChecked: Boolean,
                itemView: TaskModel
            ) {
                itemView.let { it.isTaskDone = if (isChecked) 1 else 0 }
                viewModel.updateUser(itemView)
                mainAdapter.displayList[adapterPosition].isTaskDone = if (isChecked) 1 else 0
            }


            override fun onItemClickDelete(position: Int, view: View, model: TaskModel) {
                viewModel.deleteUser(model)
                mainAdapter.removeAt(position)
            }

            override fun onItemClickExpand(
                position: Int,
                tvStartDate: View,
                linearLayout: LinearLayout,
                tvEndDate: MaterialTextView,
                tvNotifyDate: MaterialButton,
                tvCategory: MaterialButton,
                icArrow: MaterialButton,
                item: TaskModel
            ) {
                mainAdapter.isExpand = !mainAdapter.isExpand
                tvStartDate.visibility =
                    if (mainAdapter.isExpand && item.startDate.isNotEmpty()) View.VISIBLE else View.GONE
                tvCategory.visibility =
                    if (mainAdapter.isExpand && item.category.isNotEmpty()) View.VISIBLE else View.GONE
                tvEndDate.visibility =
                    if (mainAdapter.isExpand && item.endDate.isNotEmpty()) View.VISIBLE else View.GONE
                tvNotifyDate.visibility =
                    if (mainAdapter.isExpand && item.notifyTime.isNotEmpty()) View.VISIBLE else View.GONE
                icArrow.rotation = if (mainAdapter.isExpand) 180F else 360F
                linearLayout.visibility =
                    if (mainAdapter.isExpand && tvStartDate.isVisible || tvEndDate.isVisible) View.VISIBLE else View.GONE
            }
        })
    }

    private fun initView() {
        val repository = TaskRepository(TaskDatabase.getInstance(this).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        taskList.addAll(viewModel.getAllTaskLocal().toSet())

        mainAdapter = MainAdapter(taskList)
        binding.run {
            rvTask.adapter = mainAdapter
//            fabAdd.setColorFilter(
//                ContextCompat.getColor(
//                    this@MainActivity,
//                    R.color.colorPrimaryDark
//                )
//            )
            val c = Calendar.getInstance()
            when (c.get(Calendar.HOUR_OF_DAY)) {
                in 0..11 -> {
                    mtvWelcome.text = getString(R.string.hello_good_morning)
                }

                in 12..15 -> {
                    mtvWelcome.text = getString(R.string.hello_good_afternoon)
                }

                in 16..20 -> {
                    mtvWelcome.text = getString(R.string.hello_good_evening)
                }

                else -> {}
            }
            val df = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val formattedDate: String = df.format(c.time)
            mtvTime.text = formattedDate
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAdd -> modalBottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
    }
}

class AddTaskBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding1: BottomShitCreateTaskBinding
    private lateinit var viewModel: MainViewModel
    private var categoryList = arrayOf("Home", "School", "Work", "Personal", "Other")
    private lateinit var taskModel: TaskModel
    private val subTaskList = ArrayList<SubTaskModel>()
    private val attachments = ArrayList<String>()

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
                createTask.text = "Update Task"
                etTask.setText(taskModel.task)
                mbStartDate.text = taskModel.startDate
                mbEndDate.text = taskModel.endDate
                tvCategory.setText(taskModel.category)
                icAddDateTime.text = taskModel.notifyTime
                icAddStar.isChecked = taskModel.isFavorite == 1
                icPin.isChecked = taskModel.isPined == 1
            }
        }
        return binding1.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ArrayList<SubTaskModel>()
        val repository = TaskRepository(TaskDatabase.getInstance(requireContext()).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
//        setBottomShitBackground()
        binding1.run {
            tvCategory.setSimpleItems(categoryList)
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
                createTask(binding1)
                dismiss()
            }
        }
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
            }
        }
    }

    private fun pasteTask(binding1: BottomShitCreateTaskBinding) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val pasteData: String
        if (clipboard?.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
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