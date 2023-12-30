package com.example.mytodo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytodo.model.Category
import com.example.mytodo.model.TaskModel

@androidx.room.Dao
interface TaskDao {
    @Insert
    fun insertTask(taskModel: TaskModel)

    @Update
    fun editTask(taskModel: TaskModel)

    @Delete
    fun deleteTask(taskModel: TaskModel)

    @Query("SELECT * from task_table")
    fun getAllTask(): LiveData<List<TaskModel>>

    @Query("SELECT * from task_table")
    fun getAllTaskLocal(): List<TaskModel>

    @Insert
    fun insertCategory(category: Category)

    @Update
    fun editCategory(category: Category)

    @Update
    fun updateCateList(category: List<Category>)

    @Delete
    fun deleteCategory(category: Category)

    @Delete
    fun deleteCategoryList(category: List<Category>)

    @Query("SELECT * from category_table")
    fun getAllCategory(): List<Category>

}