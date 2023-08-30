package com.example.mybox.ui.detailItemScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.mybox.data.Result
import com.example.mybox.data.model.DetailModel
import com.example.mybox.databinding.ActivityDetailItemBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.addItemScreen.AddItemActivity
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.DETAIL_ID

class DetailItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailItemBinding
    private val viewModel by viewModels<DetailItemViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(DETAIL_ID) !!.toInt()
        val categoryId = intent.getStringExtra(BOX_ID) !!.toInt()

        viewModel.getDetailBox(id, categoryId).observe(this) {result->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        setDetailItem(result.data)
                    }
                    is Result.Error -> {
                        showLoading(false)
                    }
                }
            }
        }

        binding.floatingActionButton3.setOnClickListener {
            val intent = Intent(this@DetailItemActivity, AddItemActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setDetailItem(data : DetailModel) {
        binding.apply {
            Glide.with(this@DetailItemActivity)
                .load(data.ImageURL)
                .into(iVperItem)

            tVItemName.text = data.Name
            tVTimeStamp.text = data.timeStamp.toString()
        }
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar.isVisible = loading

    }
}