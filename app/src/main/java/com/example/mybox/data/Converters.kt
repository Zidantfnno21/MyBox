package com.example.mybox.data

import androidx.room.TypeConverter
import com.example.mybox.data.model.DetailModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDetailModelList(detailModelList: List<DetailModel>?): String {
        return gson.toJson(detailModelList)
    }

    @TypeConverter
    fun toDetailModelList(detailModelString: String): List<DetailModel> {
        return gson.fromJson(detailModelString, object : TypeToken<List<DetailModel>>() {}.type)
            ?: emptyList()
    }
}