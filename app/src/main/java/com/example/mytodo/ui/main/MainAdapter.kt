package com.example.mytodo.ui.main

import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.databinding.ListItemTaskBinding
import com.example.mytodo.model.TaskModel
import com.example.mytodo.utils.TaskClickListener

class MainAdapter(
    private var itemList: ArrayList<TaskModel>,
) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    private var itemClickListener: TaskClickListener? = null
    var displayList = itemList
    var isExpand = false
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
        lateinit var taskItem: TaskModel
        fun bind(item: TaskModel, itemClickListener: TaskClickListener?) {
            taskItem = item
            binding.run {
                tvTask.text = item.task
                tvNotifyDate.text = item.notifyTime
                tvStartDate.text = item.startDate
                tvEndDate.text = item.endDate
                tvTask.text = item.task
                tvCategory.text = item.category
                icPin.isVisible = item.isPined == 1
                icStar.isVisible = item.isFavorite == 1
                materialCheckBox.isChecked = item.isTaskDone == 1
                icDeleteTask.isVisible = item.isTaskDone == 1
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
            binding.icArrow.setOnClickListener {
                itemClickListener?.onItemClickExpand(
                    adapterPosition,
                    binding.tvStartDate,
                    binding.linearLayout,
                    binding.tvEndDate,
                    binding.tvNotifyDate,
                    binding.tvCategory,
                    binding.icArrow,
                    item
                )
            }


        }
    }

    fun addAll(dataList: Collection<TaskModel>) {
        itemList.clear()
        itemList.addAll(dataList.toSet())

        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    fun addFilter(dataList: Collection<TaskModel>) {
        itemList.addAll(dataList.toSet())
        notifyDataSetChanged()
    }

    fun addItem(item: TaskModel) {
        itemList.add(item)
        notifyItemInserted(itemList.size)
    }

    fun removeAt(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }
}

