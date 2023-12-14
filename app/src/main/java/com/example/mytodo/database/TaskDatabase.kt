package com.example.mytodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodo.database.dao.TaskDao
import com.example.mytodo.model.TaskModel
import com.example.mytodo.utils.Converters

@Database(entities = [TaskModel::class], version = 2)
@TypeConverters(Converters::class)

abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: TaskDatabase? = null
        fun getInstance(context: Context): TaskDatabase {
            if (INSTANCE == null) {
                synchronized(TaskDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private const val DATABASE_NAME = "task_database"

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Define your migration logic here
//                db.execSQL("ALTER TABLE task_table ADD COLUMN notes TEXT DEFAULT ''")
//                db.execSQL("ALTER TABLE task_table ADD COLUMN attachments TEXT DEFAULT ''")

            }
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                DATABASE_NAME
            ).allowMainThreadQueries().addMigrations(MIGRATION_1_2).build()
    }

}