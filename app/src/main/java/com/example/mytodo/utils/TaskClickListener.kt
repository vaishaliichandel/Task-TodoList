package com.example.mytodo.utils

import android.view.View
import android.widget.CompoundButton
import com.example.mytodo.model.TaskModel

interface TaskClickListener {
    fun onItemClick(position: Int, view: View, model: TaskModel)
    fun onItemCheckedListener(
        adapterPosition: Int,
        buttonView: CompoundButton,
        isChecked: Boolean,
        itemView: TaskModel
    )

    fun onItemClickDelete(position: Int, view: View, model: TaskModel)
}
