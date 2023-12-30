package com.example.mytodo.utils

import com.example.mytodo.model.Category
import com.example.mytodo.model.TaskModel

interface DataInsertionListener {
    fun onDataInserted(data: TaskModel)
}

interface DataFilterListener {
    fun onDataFilter(data: Category)
}