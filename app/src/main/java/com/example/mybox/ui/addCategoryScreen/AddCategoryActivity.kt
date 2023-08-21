package com.example.mybox.ui.addCategoryScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.databinding.ActivityCategoryAddBinding

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCategoryAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}