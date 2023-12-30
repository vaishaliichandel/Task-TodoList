package com.example.mytodo.ui.taskLists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.model.Category
import com.example.mytodo.model.TaskModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: TaskRepository
) : ViewModel() {
    fun getAllCate(): List<Category> {
        return userRepository.getAllCate()
    }

    fun getAllTaskLocal(): List<TaskModel> {
        return userRepository.getAllTaskLocal()
    }

    fun insertUser(user: TaskModel) {
        viewModelScope.launch {
            userRepository.insertTask(user)
        }
    }

    fun updateUser(user: TaskModel) {
        viewModelScope.launch {
            userRepository.updateTask(user)
        }
    }

    fun deleteUser(user: TaskModel) {
        viewModelScope.launch {
            userRepository.deleteTask(user)
        }
    }

}