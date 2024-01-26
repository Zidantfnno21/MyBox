package com.example.mybox.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.ui.addCategoryScreen.CameraActivity
import java.io.File

class ChooseImageAction(private val activity: AppCompatActivity) {

    private var onImageSelectedCallback: ((Uri) -> Unit)? = null

    private var onCameraCapturedCallback: ((File) -> Unit)? = null

    fun setOnImageSelectedCallback(callback: (Uri) -> Unit) {
        onImageSelectedCallback = callback
    }

    fun setOnImageCapturedCallback(callback : (File) -> Unit) {
        onCameraCapturedCallback = callback
    }

    fun showChooser() {
        val options = arrayOf<CharSequence>("Take Picture" , "Pick Picture from Gallery")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Action")
        builder.setItems(options) { _ , which ->
            when(which){
                0 -> {
                    takePhoto()
                }
                1 -> {
                    choosePhoto()
                }
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val intent = Intent(activity, CameraActivity::class.java)
        launcherCameraX.launch(intent)
    }

    private fun choosePhoto() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent , "Pick a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                onImageSelectedCallback?.invoke(uri)
            }
        }
    }

    private val launcherCameraX = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let {file ->
                rotateFile(file, isBackCamera)
                onCameraCapturedCallback?.invoke(file)
            }
        }
    }

}