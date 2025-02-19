package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@kotlinx.parcelize.Parcelize
@Entity(
    tableName = "Details",
    foreignKeys = [
        ForeignKey(
            entity = CategoryModel::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")],
)
@IgnoreExtraProperties
data class DetailModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    val categoryId : Int? = 0,
    val name : String? = "",
    val timeStamp : Long? = 0 ,
    val imageURL : String? = "",
    val isSynced: Boolean = false,
) : Parcelable
