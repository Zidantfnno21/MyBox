package com.example.mybox.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithDetails(
    @Embedded val category: CategoryModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val details: List<DetailModel>
)
