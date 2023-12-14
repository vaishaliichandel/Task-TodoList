package com.example.mytodo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.model.TaskModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: TaskRepository
) : ViewModel() {
//    fun getAllUsers(): List<TaskModel> {
//        viewModelScope.launch {
//            return@launch userRepository.getAllTask()
//        }
//    }

    fun getAllUsers(): LiveData<List<TaskModel>> {
        return userRepository.getAllTask()
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