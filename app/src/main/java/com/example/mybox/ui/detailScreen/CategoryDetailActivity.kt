package com.example.mybox.ui.detailScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.databinding.ActivityCategoryDetailBinding

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCategoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}