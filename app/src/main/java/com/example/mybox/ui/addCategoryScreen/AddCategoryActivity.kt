package com.example.mybox.ui.addCategoryScreen

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.databinding.ActivityCategoryAddBinding
import com.example.mybox.notification.NotificationHelper
import com.example.mybox.ui.detailScreen.CategoryDetailActivity
import com.example.mybox.utils.BOX
import com.example.mybox.utils.BOX_ID
import com.example.mybox.utils.ChooseImageAction
import com.example.mybox.utils.PermissionHandler
import com.example.mybox.utils.PreferencesHelper
import com.example.mybox.utils.hideKeyboard
import com.example.mybox.utils.makeToast
import com.example.mybox.utils.reduceFileImage
import com.example.mybox.utils.scrollToShowView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddCategoryActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding : ActivityCategoryAddBinding
    private lateinit var notificationHelper : NotificationHelper
    private val permissionHandler = PermissionHandler(this)
    private val imageChooser = ChooseImageAction(this)
    private var getFile: Uri? = null
    private var globalCategoryId: Int? = 0
    private val viewModel: AddCategoryViewModel by viewModels()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionsResult(requestCode , permissions)
    }

    @Suppress("DEPRECATION")
    override fun onStart() {
        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BOX_ID , CategoryModel::class.java)
        } else {
            intent.getParcelableExtra(BOX_ID)
        }

        if (category != null){
            startWithParsedData(category)
        }
        super.onStart()
    }

    private fun startWithParsedData(category : CategoryModel) {
        globalCategoryId = category.id

        getFile = Uri.parse(category.imageURL)

        Glide.with(this)
            .load(category.imageURL)
            .into(binding.profilePicture3)
        binding.addEtTitle.setText(category.name.toString())

        binding.addEtDesc.setText(category.description.toString())


        binding.apply {
            addBtSave.text = getString(R.string.update)
            addCategoryAppbarText.text = getString(R.string.update)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()

        permissionHandler.requestPermissionsIfNecessary()

        notificationHelper = NotificationHelper(this)

        binding.imageButton2.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btChangePic.setOnClickListener {
            openImageAction()
        }

        imageChooser.setOnImageSelectedCallback {uri->
            getFile = uri
            binding.profilePicture3.setBackgroundResource(android.R.color.transparent)
            Glide.with(this@AddCategoryActivity)
                .load(uri)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(binding.profilePicture3)
        }

        imageChooser.setOnImageCapturedCallback {file ->
            getFile = Uri.fromFile(file)
            Glide.with(this@AddCategoryActivity)
                .load(BitmapFactory.decodeFile(file.path))
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(binding.profilePicture3)
        }

        binding.addBtSave.setOnClickListener {
            uploadImage()
        }

        val textInputLayouts = listOf(
            binding.addTilTitle,
            binding.addTilDesc,
        )

        binding.root.viewTreeObserver.addOnPreDrawListener {
            val rect = android.graphics.Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight <= screenHeight * 0.15) {
                // Keyboard is closed
                textInputLayouts.forEach { it.clearFocus() }
            }

            true
        }

        textInputLayouts.forEach { til ->
            til.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            til.requestFocus()
                        } , 100
                    )
                    scrollToShowView(binding.scrollview, binding.addCategoryContainer)
                }
            }
        }
    }


    private fun openImageAction() {
        imageChooser.showChooser()
    }

    private fun uploadImage() {
        val uid = preferencesHelper.getSavedUsername()

        val titleText = binding.addEtTitle.text.toString()
        if (titleText.isEmpty()) binding.addTilTitle.error = "Title cannot Empty"

        val descriptionText = binding.addEtDesc.text
        val compressedImg = getFile?.let {
            if (it.scheme == "https"){
                it
            } else {
                reduceFileImage(it , applicationContext.contentResolver)
            } ?: run {
                null
            }
        }

        val model = CategoryModel(
            id = globalCategoryId,
            name = titleText,
            description = descriptionText.toString()
        )

        if (compressedImg != null && binding.addEtTitle.text !!.isNotEmpty() && uid.isNotEmpty()) {
            notificationHelper.showProgressNotification(0)
            viewModel.postCategory(compressedImg,model) { result ->
                when (result) {
                    is Result.Error -> {
                        showLoading(false)

                        notificationHelper.dismissNotification()
                        notificationHelper.failureNotification(result.error)

                        makeToast(this, result.error)
                    }
                    is Result.Loading -> {
                        showLoading(true)
                        viewModel.uploadProgress.observe(this@AddCategoryActivity){ progressValue->
                            notificationHelper.showProgressNotification(progressValue)
                        }
                    }
                    is Result.Success -> {
                        showLoading(false)
                        hideKeyboard()
                        makeToast(this, "Success Creating Category")
                        val notificationIntent = Intent(this, CategoryDetailActivity::class.java)
                        notificationIntent.putExtra(BOX, model)

                        val pendingIntent = TaskStackBuilder.create(this).run {
                            addNextIntentWithParentStack(notificationIntent)
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }

                        notificationHelper.dismissNotification()
                        notificationHelper.updateNotification("Your Category Box's is ready!", pendingIntent)

                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
//            viewModel.postCategory(uid , compressedImg , model) { result->
//                when (result) {
//                    is Result.Success -> {
//                        showLoading(false)
//
//                        hideKeyboard()
//
//                        makeToast(this, "Success Creating Category")
//
//                        val notificationIntent = Intent(this, CategoryDetailActivity::class.java)
//                        notificationIntent.putExtra(BOX, result.data)
//
//                        val pendingIntent = TaskStackBuilder.create(this).run {
//                            addNextIntentWithParentStack(notificationIntent)
//                            getPendingIntent(
//                                0,
//                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                            )
//                        }
//
//                        notificationHelper.dismissNotification()
//                        notificationHelper.updateNotification("Your Category Box's is ready!", pendingIntent)
//
//                        onBackPressedDispatcher.onBackPressed()
//                    }
//                    is Result.Error -> {
//                        showLoading(false)
//
//                        notificationHelper.dismissNotification()
//                        notificationHelper.failureNotification(result.error)
//
//                        makeToast(this, result.error)
//                    }
//                    is Result.Loading -> {
//                        showLoading(true)
//                        viewModel.uploadProgress.observe(this@AddCategoryActivity){ progressValue->
//                            notificationHelper.showProgressNotification(progressValue)
//                        }
//                    }
//                }
//            }
        } else if (getFile == null) {
            binding.profilePicture3.setBackgroundColor(ContextCompat.getColor(this@AddCategoryActivity , com.google.android.material.R.color.design_default_color_error))
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