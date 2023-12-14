package com.example.mytodo.utils

import androidx.room.TypeConverter
import com.example.mytodo.model.SubTaskModel
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson


object Converters {
    @TypeConverter
    fun fromString(value: String?): ArrayList<SubTaskModel> {
        val listType = object : TypeToken<ArrayList<SubTaskModel?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<SubTaskModel?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromAttachmentsString(value: String?): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromAttachmentsArrayList(list: ArrayList<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}