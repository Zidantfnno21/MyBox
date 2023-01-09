package com.example.mybox

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.tasks.Task

data class CategoryModel(
    val Id : String? = null ,
    val Name : String? = null ,
    val Description : String? = null ,
    val ImageURL :String? = null ,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Id)
        parcel.writeString(Name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryModel> {
        override fun createFromParcel(parcel: Parcel): CategoryModel {
            return CategoryModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoryModel?> {
            return arrayOfNulls(size)
        }
    }
}
