package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "box")
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val Id : Int ,
    val Name : String,
    val Description : String ,
    val ImageURL : String ,
    val item: List<DetailModel> = emptyList() ,
) : Parcelable
