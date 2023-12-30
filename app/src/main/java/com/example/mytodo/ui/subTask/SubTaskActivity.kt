package com.example.mytodo.ui.subTask

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.ActivitySubTaskBinding
import com.example.mytodo.model.Category
import com.example.mytodo.model.SubTaskModel
import com.example.mytodo.model.TaskModel
import com.example.mytodo.ui.category.CategoryNameAdapter
import com.example.mytodo.ui.taskLists.MainViewModel
import com.example.mytodo.utils.CategoryListener
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
    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var taskData: TaskModel
    private lateinit var adapter: CategoryNameAdapter
    private var subTaskList = ArrayList<SubTaskModel>()
    private var attachmentList = ArrayList<String>()
    private var categoryList = ArrayList<Category>()
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
        binding.icAdd.setOnClickListener(this)
        binding.icEdit.setOnClickListener(this)
        binding.icCopy.setOnClickListener(this)
        binding.icDelete.setOnClickListener(this)
        subTaskAdapter.setOnClickListener(object : SubTaskClickListener {
            override fun onItemClick(position: Int) {
                subTaskAdapter.removeAt(position)
            }
        })
        adapter.clickListener(object : CategoryListener {
            override fun clickListener(category: Category, adapterPosition: Int, view: View) {
                binding.tvCategory.setText(category.name)
            }

            override fun clickItemListener(
                category: Category,
                adapterPosition: Int,
                view: View
            ) {
            }
        })
    }

    private fun initView() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#000037")
        val repository = TaskRepository(TaskDatabase.getInstance(this).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        taskData = intent.getSerializableExtra("TASK_DATA") as TaskModel
        subTaskList = taskData.subClass!!
        taskData.attachments?.let { attachmentList.addAll(it) }
        if (viewModel.getAllCate().isEmpty()) {
            categoryList.add(Category(name = "Home", color = R.color.colorHome))
            categoryList.add(Category(name = "Personal", color = R.color.colorPersonal))
            categoryList.add(Category(name = "School", color = R.color.colorSchool))
            categoryList.add(Category(name = "Work", color = R.color.colorWork))
        }
        categoryList.addAll(viewModel.getAllCate().filter { it.isHide == 0 })
        adapter = CategoryNameAdapter(
            this,
            R.layout.layout_edittext, R.id.tvName, categoryList
        )
        binding.run {
            tvCategory.setAdapter(adapter)
            subTaskAdapter = SubTaskAdapter(taskData.subClass!!)
            attachmentAdapter = AttachmentAdapter(attachmentList)
            tvSubTask.adapter = subTaskAdapter
            rvAttachment.adapter = attachmentAdapter
            tvTask.setText(taskData.task)
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
                tvReminder.text = getString(R.string.no)
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

            R.id.icAdd -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                changeImage.launch(Intent.createChooser(intent, "Pictures: "))
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

    private val changeImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {

            if (it.data?.clipData != null) {
                val count: Int = it.data?.clipData!!.itemCount
                for (i in 0 until count) {
                    attachmentList.add(it.data!!.clipData?.getItemAt(i)?.uri.toString())
                }
                attachmentAdapter.notifyDataSetChanged()
            }
        }
    }
}