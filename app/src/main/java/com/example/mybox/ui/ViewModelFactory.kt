@file:Suppress("UNCHECKED_CAST")

package com.example.mybox.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mybox.data.BoxRepository
import com.example.mybox.ui.addCategoryScreen.AddCategoryViewModel
import com.example.mybox.ui.addItemScreen.AddItemViewModel
import com.example.mybox.ui.detailScreen.CategoryDetailViewModel
import com.example.mybox.ui.mainScreen.MainViewModel
import com.example.mybox.utils.Injection

class ViewModelFactory private constructor(private val boxRepository : BoxRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(boxRepository) as T
        }else if (modelClass.isAssignableFrom(CategoryDetailViewModel::class.java)){
            return CategoryDetailViewModel(boxRepository) as T
        }else if (modelClass.isAssignableFrom(AddItemViewModel::class.java)){
            return AddItemViewModel(boxRepository) as T
        }else if (modelClass.isAssignableFrom(AddCategoryViewModel::class.java)){
            return AddCategoryViewModel(boxRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context : Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}