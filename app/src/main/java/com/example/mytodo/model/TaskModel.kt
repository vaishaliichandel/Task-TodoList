package com.example.mytodo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "task_table")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val task: String,
    @ColumnInfo val subClass: ArrayList<SubTaskModel>?,
    @ColumnInfo val startDate: String,
    @ColumnInfo val endDate: String,
    @ColumnInfo val category: String,
    @ColumnInfo val notifyTime: String,
    @ColumnInfo val isPined: Int,
    @ColumnInfo val isFavorite: Int,
    @ColumnInfo var isTaskDone: Int = 0,
    @ColumnInfo val notes: String = "",
    @ColumnInfo val attachments: ArrayList<String>?,
) : Serializable