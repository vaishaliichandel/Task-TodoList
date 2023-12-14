package com.example.mytodo.ui.subTask

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.databinding.ListItemSubTaskBinding
import com.example.mytodo.model.SubTaskModel
import com.example.mytodo.utils.SubTaskClickListener


class SubTaskAdapter(private val displayList: ArrayList<SubTaskModel>) :
    RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>() {
    private var itemClickListener: SubTaskClickListener? = null

    class SubTaskViewHolder(private val binding: ListItemSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subTaskModel: SubTaskModel, itemClickListener: SubTaskClickListener?) {
            binding.run {
                etSubTask.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int,
                        count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        subTaskModel.subTask = s.toString()
                    }
                })
                etSubTask.setText(subTaskModel.subTask)
                ivOption.setOnCheckedChangeListener(null)
                ivOption.isChecked = subTaskModel.isComplete
                ivOption.setOnCheckedChangeListener(null)
                ivOption.setOnCheckedChangeListener { _, isChecked ->
                    subTaskModel.isComplete = isChecked
                    if (subTaskModel.isComplete) {
                        etSubTask.paintFlags = etSubTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else
                        etSubTask.paintFlags = Paint.ANTI_ALIAS_FLAG

                }
                ivClose.setOnClickListener {
                    itemClickListener?.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSubTaskBinding.inflate(inflater, parent, false)
        return SubTaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.bind(displayList[position], itemClickListener)

    }

    fun setOnClickListener(onClickListener: SubTaskClickListener) {
        this.itemClickListener = onClickListener
    }

    fun addAll(dataList: Collection<SubTaskModel>) {
        displayList.clear()
        displayList.addAll(dataList.toSet())
        notifyDataSetChanged()
    }

    fun addItem(item: SubTaskModel) {
        displayList.add(item)
        notifyItemInserted(displayList.size)
    }

    fun removeAt(position: Int) {
        displayList.removeAt(position)
        notifyItemRemoved(position)
    }
}