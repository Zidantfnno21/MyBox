package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@kotlinx.parcelize.Parcelize
@Entity(tableName = "Category")
@IgnoreExtraProperties
data class CategoryModel(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = 0 ,
    val name : String? = "",
    val description : String? = "" ,
    val imageURL : String? = "" ,
    val isSynced: Boolean = false,
) : Parcelable

