package com.example.mytodo.utils

import android.view.View
import com.example.mytodo.model.Category

interface CategoryListener {
    fun clickListener(category: Category, adapterPosition: Int, view: View)
    fun clickItemListener(category: Category, adapterPosition: Int, view: View)
}