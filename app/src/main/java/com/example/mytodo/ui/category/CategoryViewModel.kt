package com.example.mytodo.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.model.Category
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepo: TaskRepository
) : ViewModel() {
    fun getAllCate(): List<Category> {
        return categoryRepo.getAllCate()
    }

    fun insertCate(cate: Category) {
        viewModelScope.launch {
            categoryRepo.insertCate(cate)
        }
    }

    fun updateCate(cate: Category) {
        viewModelScope.launch {
            categoryRepo.updateCate(cate)
        }
    }

    fun deleteCate(cate: Category) {
        viewModelScope.launch {
            categoryRepo.deleteCate(cate)
        }
    }

    fun deleteCategoryList(user: List<Category>) {
        viewModelScope.launch {
            categoryRepo.deleteCategoryList(user)
        }
    }
}