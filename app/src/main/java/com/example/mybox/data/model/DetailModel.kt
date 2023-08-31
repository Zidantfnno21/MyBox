package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "boxDetails")
data class DetailModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val categoryId : Int,
    val name : String,
    val timeStamp : Long ,
    val imageURL : String
) : Parcelable
