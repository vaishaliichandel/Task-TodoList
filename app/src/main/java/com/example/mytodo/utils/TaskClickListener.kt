package com.example.mytodo.utils

import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.example.mytodo.model.TaskModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

interface TaskClickListener {
    fun onItemClick(position: Int, view: View, model: TaskModel)
    fun onItemCheckedListener(
        adapterPosition: Int,
        buttonView: CompoundButton,
        isChecked: Boolean,
        itemView: TaskModel
    )
    fun onItemClickDelete(position: Int, view: View, model: TaskModel)
    fun onItemClickExpand(
        position: Int,
        tvStartDate: View,
        linearLayout: LinearLayout,
        tvEndDate: MaterialTextView,
        tvNotifyDate: MaterialButton,
        tvCategory: MaterialButton,
        icArrow: MaterialButton,
        item: TaskModel
    )
}
