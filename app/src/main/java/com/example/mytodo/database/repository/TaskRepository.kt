package com.example.mytodo.database.repository

import com.example.mytodo.database.dao.TaskDao
import com.example.mytodo.model.Category
import com.example.mytodo.model.TaskModel

class TaskRepository(private val taskDao: TaskDao) {
    fun insertTask(task: TaskModel) = taskDao.insertTask(task)
    fun updateTask(task: TaskModel) = taskDao.editTask(task)
    fun deleteTask(task: TaskModel) = taskDao.deleteTask(task)
    fun getAllTaskLocal() = taskDao.getAllTaskLocal()
    fun insertCate(category: Category) = taskDao.insertCategory(category)
    fun updateCate(category: Category) = taskDao.editCategory(category)
    fun deleteCate(category: Category) = taskDao.deleteCategory(category)
    fun getAllCate(): List<Category> = taskDao.getAllCategory()
    fun deleteCategoryList(list: List<Category>) = taskDao.deleteCategoryList(list)
}