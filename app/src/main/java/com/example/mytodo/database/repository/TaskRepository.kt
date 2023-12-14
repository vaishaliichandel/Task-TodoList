package com.example.mytodo.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mytodo.database.dao.TaskDao
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.model.TaskModel

class TaskRepository(private val taskDao: TaskDao) {
    fun insertTask(task: TaskModel) = taskDao.insertTask(task)
    fun updateTask(task: TaskModel) = taskDao.editTask(task)
    fun deleteTask(task: TaskModel) = taskDao.deleteTask(task)
    fun getAllTask(): LiveData<List<TaskModel>>{
        return taskDao.getAllTask()
    }
    fun getAllTaskLocal(): List<TaskModel>{
        return taskDao.getAllTaskLocal()
    }
}