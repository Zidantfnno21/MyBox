package com.example.mybox.ui.detailItemScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.databinding.ActivityCategoryDetailBinding
import com.example.mybox.databinding.ActivityDetailItemBinding

class DetailItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailItemBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}