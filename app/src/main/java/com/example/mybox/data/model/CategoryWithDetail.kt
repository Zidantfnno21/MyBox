package com.example.mybox.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithDetail(
    @Embedded val category: CategoryModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val items: List<DetailModel> = emptyList()
)
