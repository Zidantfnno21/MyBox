package com.example.mybox.ui.addItemScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.databinding.ActivityItemAddBinding

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityItemAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}