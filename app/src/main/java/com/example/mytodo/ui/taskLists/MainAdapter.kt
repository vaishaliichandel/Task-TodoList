package com.example.mytodo.ui.taskLists

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.databinding.ListItemTaskBinding
import com.example.mytodo.model.TaskModel
import com.example.mytodo.utils.TaskClickListener
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainAdapter(
    var itemList: ArrayList<TaskModel>,
) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    private var itemClickListener: TaskClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTaskBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(itemList[position], itemClickListener)
    }

    fun setOnClickListener(onClickListener: TaskClickListener) {
        this.itemClickListener = onClickListener
    }

    class MainViewHolder(
        private val binding: ListItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var taskItem: TaskModel
        fun bind(item: TaskModel, itemClickListener: TaskClickListener?) {
            taskItem = item
            binding.run {
                val format = SimpleDateFormat("dd MMM yyyy h:mm a", Locale.ENGLISH)
                if (item.notifyTime.isNotEmpty()) {
                    tvNotifyDate.visible()
                    val date = format.parse(item.notifyTime)
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.time = date!!
                    calendar.get(Calendar.HOUR_OF_DAY) // Replace with your desired hour
                    calendar.get(Calendar.MINUTE) // Replace with your desired minute
                    calendar.get(Calendar.SECOND)
                    calendar.timeInMillis
                    val time = System.currentTimeMillis()
                    if (time > calendar.timeInMillis) {
                        tvNotifyDate.text = binding.root.context.getString(R.string.overdue)
                        tvNotifyDate.setIconResource(R.drawable.ic_timer)
                        tvNotifyDate.backgroundTintList =
                            ContextCompat.getColorStateList(binding.root.context, R.color.red)
                    } else {
                        tvNotifyDate.setIconResource(R.drawable.ic_notification)
                        tvNotifyDate.text = item.notifyTime
                        tvNotifyDate.backgroundTintList =
                            ContextCompat.getColorStateList(
                                binding.root.context,
                                R.color.lightTeal
                            )
                    }
                } else {
                    tvNotifyDate.gone()
                }

                tvTask.text = item.task
                tvStartDate.text = item.startDate
                tvEndDate.text = item.endDate
                tvTask.text = item.task
                tvCategory.text = item.category
                icPin.isVisible = item.isPined == 1
                icStar.isVisible = item.isFavorite == 1
                materialCheckBox.isChecked = item.isTaskDone == 1
                icDeleteTask.isVisible = item.isTaskDone == 1
//                tvNotifyDate.isVisible = tvNotifyDate.text.isNotEmpty()
                tvCategory.isVisible = tvCategory.text.isNotEmpty()
                tvEndDate.isVisible = tvEndDate.text.isNotEmpty()
                tvStartDate.isVisible = tvStartDate.text.isNotEmpty()
                if (item.isTaskDone == 1) {
                    tvTask.paintFlags = tvTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else
                    tvTask.paintFlags = Paint.ANTI_ALIAS_FLAG
            }
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition, itemView, taskItem)
            }
            binding.icDeleteTask.setOnClickListener {
                itemClickListener?.onItemClickDelete(adapterPosition, itemView, taskItem)
            }
            binding.materialCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                itemClickListener?.onItemCheckedListener(
                    adapterPosition,
                    buttonView,
                    isChecked,
                    taskItem
                )
            }
        }
    }

    fun removeAt(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addAll(toSet: Set<TaskModel>) {
        itemList.clear()
        itemList.addAll(toSet)
        notifyDataSetChanged()
    }
}

