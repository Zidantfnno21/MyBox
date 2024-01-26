package com.example.mybox.ui.detailItemScreen

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mybox.data.Result
import com.example.mybox.data.model.DetailModel
import com.example.mybox.databinding.ActivityDetailItemBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.addItemScreen.AddItemActivity
import com.example.mybox.utils.DETAIL_ID
import com.example.mybox.utils.SharedPreferences
import com.example.mybox.utils.convertTimestampToISOString

class DetailItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailItemBinding
    private val viewModel by viewModels<DetailItemViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var detailItem: DetailModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()

        detailItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DETAIL_ID , DetailModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DETAIL_ID)
        } ?: throw IllegalArgumentException("CategoryModel not found in the intent")

        binding.detailItemBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.floatingActionButton3.setOnClickListener {
            val intent = Intent(this@DetailItemActivity, AddItemActivity::class.java)
            intent.putExtra(DETAIL_ID, detailItem)
            startActivity(intent)
        }

        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        val uid = SharedPreferences().getSavedUsername(this)
        val id = detailItem.id
        val categoryId = detailItem.categoryId

        if (id != null && categoryId != null) {
            viewModel.getDetailBox(uid , id.toInt() , categoryId.toInt()).observe(this) { result->
                if (result != null) {
                    when(result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            detailItem = result.data
                            setDetailItem(detailItem)
                        }
                        is Result.Error -> {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun setDetailItem(data : DetailModel) {
        binding.apply {
            Glide.with(this@DetailItemActivity)
                .load(data.imageURL)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(iVperItem)
            tVItemName.text = data.name
            tVTimeStamp.text = data.timeStamp?.let { convertTimestampToISOString(it) }
        }
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar.background = (ColorDrawable(Color.parseColor("#63616161")))
        binding.progressBar.isVisible = loading
        if (loading){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}