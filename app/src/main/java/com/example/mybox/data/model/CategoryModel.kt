package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(tableName = "boxCategory")
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val id : Int ,
    val name : String,
    val description : String ,
    val imageURL : String ,
) : Parcelable
