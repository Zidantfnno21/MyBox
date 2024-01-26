package com.example.mybox.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
data class UserModel(
    @PrimaryKey
    val email: String? = "" ,
    val name: String? = "",
) : Parcelable
