package com.project.nasalibrary.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.nasalibrary.model.Data
import com.project.nasalibrary.model.Link

class AppTypeConvertors {
    private val gson = Gson()

    // Converter for List<Data>
    @TypeConverter
    fun fromDataList(dataList: List<Data>?): String? {
        return dataList?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toDataList(dataListString: String?): List<Data>? {
        return dataListString?.let {
            val listType = object : TypeToken<List<Data>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromLinkList(linkList: List<Link?>?): String? {
        return linkList?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLinkList(linkListString: String?): List<Link?>? {
        return linkListString?.let {
            val listType = object : TypeToken<List<Link?>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromStringList(stringList: List<String?>?): String? {
        return stringList?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(stringListString: String?): List<String?>? {
        return stringListString?.let {
            val listType = object : TypeToken<List<String?>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}