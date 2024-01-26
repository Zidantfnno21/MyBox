package com.example.mybox.ui.addItemScreen

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.data.model.DetailModel
import com.example.mybox.databinding.ActivityItemAddBinding
import com.example.mybox.notification.NotificationHelper
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.detailScreen.CategoryDetailActivity
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.ChooseImageAction
import com.example.mybox.utils.DETAIL_ID
import com.example.mybox.utils.SharedPreferences
import com.example.mybox.utils.makeToast
import com.example.mybox.utils.reduceFileImage
import java.util.Calendar

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityItemAddBinding
    private lateinit var notificationHelper : NotificationHelper
    private val imageChooser = ChooseImageAction(this)
    private val viewModel by viewModels<AddItemViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var globalCategoryId: Int? = 0
    private var globalItemId: Int? = 0
    private var getFile: Uri? = null

    @Suppress("DEPRECATION")
    override fun onStart() {
        val detail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DETAIL_ID, DetailModel::class.java)
        } else {
            intent.getParcelableExtra(DETAIL_ID)
        }

        if (detail != null) {
            startWithParsedData(detail)
        }else{
            globalCategoryId = intent.getStringExtra(BOX_ID)?.toInt() !!
        }
        super.onStart()
    }

    private fun startWithParsedData(detail : DetailModel) {
        globalItemId = detail.id
        globalCategoryId = detail.categoryId
        getFile = Uri.parse(detail.imageURL)
        Glide.with(this)
            .load(detail.imageURL)
            .into(binding.addItemIvItem)
        binding.addItemEtTitle.setText(detail.name)
        binding.addItemBtnSave.text = getString(R.string.update)
        binding.appBarAddItemText.text = getString(R.string.update)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()

        notificationHelper = NotificationHelper(this)

        binding.addItemBackBtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addItemTextBtn.setOnClickListener {
            openImageAction()
        }

        imageChooser.setOnImageSelectedCallback {uri->
            getFile = uri
            Glide.with(this@AddItemActivity)
                .load(uri)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(binding.addItemIvItem)
        }

        imageChooser.setOnImageCapturedCallback {file ->
            getFile = Uri.fromFile(file)
            Glide.with(this@AddItemActivity)
                .load(BitmapFactory.decodeFile(file.path))
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(binding.addItemIvItem)
        }

        binding.addItemBtnSave.setOnClickListener{
            push()
        }


        binding.addItemTilTitle.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.fullScroll(v.bottom)
                }
            }
        }

    }

    private fun openImageAction() {
        imageChooser.showChooser()
    }

    private fun push() {
        val uid = SharedPreferences().getSavedUsername(this)
        val title = binding.addItemEtTitle.text.toString()
        if (title.isEmpty()) binding.addItemTilTitle.error = "Title Cannot Empty"

        val time = Calendar.getInstance().timeInMillis
        val categoryId = globalCategoryId
        val compressedImg = getFile?.let {
            if (it.scheme == "https"){
                it
            } else {
                reduceFileImage(it , applicationContext.contentResolver)
            }
        } ?: run {
            null
        }

        val model = DetailModel(
            id = globalItemId,
            name = title ,
            timeStamp = time,
            categoryId = categoryId
        )

        if (binding.addItemEtTitle.text !!.isNotEmpty() && uid.isNotEmpty() && categoryId != null && compressedImg != null) {
            notificationHelper.showProgressNotification(0)

            viewModel.postItem(uid , compressedImg , model , categoryId.toInt()) { result->
                when(result) {
                    is Result.Success -> {
                        showLoading(false)

                        val notificationIntent = Intent(this, CategoryDetailActivity::class.java)
                        notificationIntent.putExtra(BOX_ID, result.data.categoryId)

                        val pendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
                            addNextIntentWithParentStack(notificationIntent)
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }

                        notificationHelper.dismissNotification()
                        notificationHelper.updateNotification("Your Items are ready too!", pendingIntent)

                        onBackPressedDispatcher.onBackPressed()
                    }
                    is Result.Error -> {
                        showLoading(false)

                        notificationHelper.dismissNotification()
                        notificationHelper.failureNotification(result.error)
                    }
                    is Result.Loading -> {
                        showLoading(true)

                        viewModel.uploadProgress.observe(this@AddItemActivity){ progressValue->
                            notificationHelper.showProgressNotification(progressValue)
                        }
                    }
                }
            }
        }else{
            binding.addItemIvItem.setBackgroundColor(ContextCompat.getColor(this@AddItemActivity , com.google.android.material.R.color.design_default_color_error))
            makeToast(this, "Sorry,but Image cannot be empty for now")

        }
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar3.isVisible = loading
        binding.progressBar3.background = (ColorDrawable(Color.parseColor("#63616161")))

        if (loading){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}