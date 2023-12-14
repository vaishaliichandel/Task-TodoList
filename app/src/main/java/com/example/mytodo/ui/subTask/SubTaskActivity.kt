package com.example.mytodo.ui.subTask

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.ActivitySubTaskBinding
import com.example.mytodo.model.SubTaskModel
import com.example.mytodo.model.TaskModel
import com.example.mytodo.ui.main.MainViewModel
import com.example.mytodo.utils.SubTaskClickListener
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import com.example.mytodo.viewModelFectory.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SubTaskActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySubTaskBinding
    private lateinit var subTaskAdapter: SubTaskAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var taskData: TaskModel
    private var subTaskList = ArrayList<SubTaskModel>()
    private var attachmentList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        clickListener()
    }

    private fun clickListener() {
        binding.tvAddSubTask.setOnClickListener(this)
        binding.tvAddNotes.setOnClickListener(this)
        binding.icDone.setOnClickListener(this)
        binding.bUpdate.setOnClickListener(this)
        binding.icEdit.setOnClickListener(this)
        binding.icCopy.setOnClickListener(this)
        binding.icDelete.setOnClickListener(this)
        subTaskAdapter.setOnClickListener(object : SubTaskClickListener {
            override fun onItemClick(position: Int) {
                subTaskAdapter.removeAt(position)
            }
        })
    }

    private fun initView() {
        val repository = TaskRepository(TaskDatabase.getInstance(this).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        taskData = intent.getSerializableExtra("TASK_DATA") as TaskModel
        subTaskList = taskData.subClass!!
        binding.run {

            subTaskAdapter = SubTaskAdapter(taskData.subClass!!)
            tvSubTask.adapter = subTaskAdapter
            tvTask.setText(taskData.task)
//            subTaskList.let { subTaskAdapter.addAll(it) }
            tvEndDate.text = taskData.endDate
            tvCategory.setText(taskData.category)
            if (taskData.endDate.isEmpty()) {
                val c = Calendar.getInstance()
                val df = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c.time)
                tvEndDate.text = formattedDate
            }
            if (taskData.notes.isEmpty()) {
                tvDesNotes.gone()
            } else {
                tvDesNotes.visible()
                tvDesNotes.setText(taskData.notes)
            }
            if (taskData.notifyTime.isEmpty()) {
                tvReminder.text = "No"
                tvReminderAt.gone()
                tvReminderTime.gone()
            } else {
                tvReminder.text = ""
                tvReminderAt.visible()
                tvReminderTime.visible()
                val dateString = taskData.notifyTime
                val inputFormat = SimpleDateFormat("dd MMM yyyy h:mm a", Locale.getDefault())
                val date: Date? = inputFormat.parse(dateString)
                val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val extractedDate: String? = date?.let { outputDateFormat.format(it) }
                val extractedTime: String? = date?.let { outputTimeFormat.format(it) }
                tvReminder.text = extractedDate
                tvReminderTime.text = extractedTime
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvAddNotes -> {
                binding.tvDesNotes.visible()
                binding.tvDesNotes.isEnabled = true
                binding.icDone.visible()
                binding.tvDesNotes.requestFocus()
            }

            R.id.bUpdate -> {
                binding.run {
                    viewModel.updateUser(
                        TaskModel(
                            id = taskData.id,
                            subClass = subTaskList,
                            task = tvTask.text.toString(),
                            endDate = tvEndDate.text.toString(),
                            category = tvCategory.text.toString(),
                            notifyTime = taskData.notifyTime, isTaskDone = taskData.isTaskDone,
                            attachments = attachmentList,
                            isFavorite = taskData.isFavorite,
                            isPined = taskData.isPined,
                            startDate = taskData.startDate, notes = tvDesNotes.text.toString()
                        )
                    )
                }
                finish()
            }

            R.id.icDone -> {
                binding.run {
                    tvDesNotes.visible()
                    tvDesNotes.isEnabled = false
                    binding.icDone.gone()
                }

                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.constraint)
                constraintSet.connect(
                    R.id.materialDivider4,
                    ConstraintSet.TOP,
                    R.id.tvDesNotes,
                    ConstraintSet.BOTTOM,
                )
                constraintSet.applyTo(binding.constraint)
            }

            R.id.tvAddSubTask -> {
                subTaskAdapter.addItem(SubTaskModel(subTask = ""))
            }

            R.id.icEdit -> {
                binding.run {
                    tvTask.isEnabled = true
                    tvTask.requestFocus()
                }
            }

            R.id.icDelete -> {
                viewModel.deleteUser(taskData)
                finish()
            }

            R.id.icCopy -> {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText(
                        binding.tvTask.text.toString(),
                        binding.tvTask.text.toString()
                    )
                clipboard.setPrimaryClip(clip)
            }
        }
    }
}