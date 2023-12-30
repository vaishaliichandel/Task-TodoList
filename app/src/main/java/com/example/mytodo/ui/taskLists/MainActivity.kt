package com.example.mytodo.ui.taskLists

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.ActivityMainBinding
import com.example.mytodo.model.Category
import com.example.mytodo.model.TaskModel
import com.example.mytodo.ui.category.BottomShitCategory
import com.example.mytodo.ui.newTask.AddTaskBottomSheet
import com.example.mytodo.ui.sorting.BottomShitSortBy
import com.example.mytodo.ui.subTask.SubTaskActivity
import com.example.mytodo.utils.DataFilterListener
import com.example.mytodo.utils.DataInsertionListener
import com.example.mytodo.utils.TaskClickListener
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import com.example.mytodo.viewModelFectory.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), OnClickListener, DataInsertionListener,
    DataFilterListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false
    private var isActiveTask = true
    private val taskList = ArrayList<TaskModel>()
    private val activeTask = ArrayList<TaskModel>()
    private val doneTask = ArrayList<TaskModel>()
    private val favTaskList = ArrayList<TaskModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        clickListeners()
    }

    private fun checkEmptyTask() {
        if (mainAdapter.itemList.isEmpty()) {
            binding.llNotFound.visible()
            binding.rvTask.gone()
        } else {
            binding.llNotFound.gone()
            binding.rvTask.visible()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickListeners() {
        binding.run {
            fabAdd.setOnClickListener(this@MainActivity)
            bDone.setOnClickListener(this@MainActivity)
            bActive.setOnClickListener(this@MainActivity)
            icSearch.setOnClickListener(this@MainActivity)
            bottomAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.category -> {
                        val bottomSheet = BottomShitCategory()
                        bottomSheet.setListener(this@MainActivity)
                        bottomSheet.show(supportFragmentManager, "CATEGORY")
                    }

                    R.id.sort -> {
                        val bottomSheet = BottomShitSortBy()
                        bottomSheet.show(supportFragmentManager, "SORT")
                    }

                    R.id.accelerator -> {
                        startFilter(item)
                    }
                }
                true
            }
            etTask.setOnTouchListener(View.OnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= binding.etTask.right - binding.etTask.getCompoundDrawables()[2].getBounds()
                            .width()
                    ) {
                        binding.etTask.text?.clear()
                        binding.outlinedTaskSearch.gone()
                        binding.icSearch.visible()
                        return@OnTouchListener true
                    }
                }
                false
            })
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
                activeDoneTask(adapterPosition, isChecked, itemView)
            }


            override fun onItemClickDelete(position: Int, view: View, model: TaskModel) {
                viewModel.deleteUser(model)
                taskList.removeAt(position)
                binding.rvTask.post { mainAdapter.notifyItemRemoved(position) }
            }
        })
    }

    private fun activeDoneTask(adapterPosition: Int, isChecked: Boolean, itemView: TaskModel) {
        val taskModel = TaskModel(
            id = itemView.id,
            subClass = itemView.subClass,
            task = itemView.task,
            startDate = itemView.startDate,
            endDate = itemView.endDate,
            category = itemView.category,
            notifyTime = itemView.notifyTime,
            isPined = itemView.isPined,
            isFavorite = itemView.isFavorite,
            attachments = itemView.attachments,
            isTaskDone = if (isChecked) 1 else 0
        )
        viewModel.updateUser(taskModel)
        mainAdapter.itemList[adapterPosition].isTaskDone = if (isChecked) 1 else 0
        binding.rvTask.post { mainAdapter.notifyItemChanged(adapterPosition) }

        if (isChecked && isActiveTask) {
            binding.rvTask.post {
                activeTask.remove(taskModel)
                mainAdapter.removeAt(adapterPosition)
                doneTask.add(taskModel)
                val total = activeTask.size + doneTask.size
                binding.tvTaskProgress.text =
                    doneTask.size.toString().plus(" / ").plus(total.toString())
                        .plus(" Tasks Completed")
            }
        }

        if (!isChecked && !isActiveTask) {
            binding.rvTask.post {
                doneTask.remove(taskModel)
                mainAdapter.removeAt(adapterPosition)
                activeTask.add(taskModel)
                val total = activeTask.size + doneTask.size
                binding.tvTaskProgress.text =
                    doneTask.size.toString().plus(" / ").plus(total.toString())
                        .plus(" Tasks Completed")
            }

        }
    }

    private fun startFilter(item: MenuItem) {
        if (isActiveTask) {
            if (isFavorite) {
                item.setIcon(R.drawable.ic_unfilled_star)
                mainAdapter.addAll(activeTask.toSet())
                binding.rvTask.post { mainAdapter.notifyDataSetChanged() }
            } else {
                item.setIcon(R.drawable.ic_filled_star)
                mainAdapter.addAll(activeTask.filter { it.isFavorite == 1 }.toSet())
                binding.rvTask.post { mainAdapter.notifyDataSetChanged() }
            }
        } else {
            if (isFavorite) {
                item.setIcon(R.drawable.ic_unfilled_star)
                mainAdapter.addAll(doneTask.toSet())
                binding.rvTask.post { mainAdapter.notifyDataSetChanged() }
            } else {
                item.setIcon(R.drawable.ic_filled_star)
                mainAdapter.addAll(doneTask.filter { it.isFavorite == 1 }.toSet())
                binding.rvTask.post { mainAdapter.notifyDataSetChanged() }
            }
        }
        checkEmptyTask()
        isFavorite = !isFavorite
    }

    private fun initView() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#000037")
        val repository = TaskRepository(TaskDatabase.getInstance(this).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        taskList.addAll(viewModel.getAllTaskLocal())
        favTaskList.addAll(viewModel.getAllTaskLocal().toSet())
        activeTask.addAll(viewModel.getAllTaskLocal().filter { it.isTaskDone != 1 }.toSet())
        doneTask.addAll(viewModel.getAllTaskLocal().filter { it.isTaskDone == 1 }.toSet())

        mainAdapter = MainAdapter(taskList)
        binding.run {
            tvTaskProgress.isVisible = mainAdapter.itemList.isNotEmpty()
            mainAdapter.addAll(activeTask.toSet())
            rvTask.adapter = mainAdapter
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
            val total = activeTask.size + doneTask.size
            binding.tvTaskProgress.text =
                doneTask.size.toString().plus(" / ").plus(total.toString())
                    .plus(" Tasks Completed")
            checkEmptyTask()
        }

    }

    @SuppressLint("ResourceAsColor")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAdd -> {
                val modalBottomSheet = AddTaskBottomSheet()
                modalBottomSheet.setListener(this)
                modalBottomSheet.show(supportFragmentManager, "ModalBottomSheet")
            }

            R.id.icSearch -> {
                binding.outlinedTaskSearch.visible()
            }

            R.id.bActive -> {
                isActiveTask = true
                binding.run {
                    bActive.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    bDone.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    select.animate().x(0F).setDuration(100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isFavorite) mainAdapter.addAll(activeTask.filter { it.isFavorite == 1 }
                            .toSet()) else mainAdapter.addAll(
                            activeTask.toSet()
                        )
                        checkEmptyTask()
                    }, 200)
                }
            }

            R.id.bDone -> {
                isActiveTask = false
                binding.run {
                    bActive.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                    bDone.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    val size = bDone.width
                    select.animate().x(size.toFloat()).setDuration(100)

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isFavorite) mainAdapter.addAll(doneTask.filter { it.isFavorite == 1 }
                            .toSet()) else mainAdapter.addAll(
                            doneTask.toSet()
                        )
                        checkEmptyTask()
                    }, 200)
                }
            }
        }
    }

    override fun onDataInserted(data: TaskModel) {
        Log.e("data", data.task)
        taskList.add(data)
        mainAdapter.notifyItemInserted(taskList.size)
    }

    override fun onDataFilter(data: Category) {
        Log.e("data", data.name)
        if (isActiveTask)
            mainAdapter.addAll(activeTask.filter { it.category == data.name }.toSet())
        else
            mainAdapter.addAll(doneTask.filter { it.category == data.name }.toSet())
        checkEmptyTask()
    }
}

