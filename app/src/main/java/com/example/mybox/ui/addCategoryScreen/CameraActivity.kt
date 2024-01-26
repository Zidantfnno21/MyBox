package com.example.mybox.ui.addCategoryScreen

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.mybox.R
import com.example.mybox.databinding.ActivityCameraBinding
import com.example.mybox.utils.CAMERA_X_RESULT
import com.example.mybox.utils.createFile

class CameraActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCameraBinding
    private val flashModes = arrayOf(
        ImageCapture.FLASH_MODE_ON,
        ImageCapture.FLASH_MODE_OFF,
        ImageCapture.FLASH_MODE_AUTO
    )
    private var flashIndex = 0
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()

        startCamera()

        binding.snapButton.setOnClickListener {
            captureImage()
        }

        binding.flipButton.setOnClickListener {
            val rotate = ObjectAnimator.ofFloat(binding.flipButton , "rotation" , 0f , 180f)
            rotate.duration = 500
            rotate.start()

            val transition = AutoTransition()
            transition.duration = 500
            TransitionManager.beginDelayedTransition(binding.root, transition)
            switchCamera()
        }

        binding.flash.setOnClickListener {
            flashConfiguration()
        }

        binding.close.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        flashConfiguration()
        startCamera()
    }

    private fun captureImage() {
        val imageCapture = imageCapture?: return
        val file = createFile(application)
        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(file)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults : ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("picture", file)
                    intent.putExtra(
                        "isBackCamera",
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(CAMERA_X_RESULT, intent)
                    finish()
                }

                override fun onError(exception : ImageCaptureException) {
                    Toast.makeText(this@CameraActivity , "Failed to capture image" , Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this)

        cameraProvider.addListener(
            {
                val cameraProv: ProcessCameraProvider = cameraProvider.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()

                try {
                    cameraProv.unbindAll()
                    cameraProv.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Toast.makeText(this@CameraActivity , "Failed to open camera" , Toast.LENGTH_SHORT).show()
                }
            }, ContextCompat.getMainExecutor(this))
    }


    private fun flashConfiguration() {
        if (imageCapture != null) {
            flashIndex = (flashIndex + 1) % flashModes.size
            val newFlashMode = flashModes[flashIndex]
            imageCapture!!.flashMode = newFlashMode
            updateFlashUi(newFlashMode)
        }
    }

    private fun updateFlashUi(newFlashModes : Int) {
        when(newFlashModes) {
            0 -> {
                val newIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_flash_off, null)
                binding.flash.icon = newIcon
            }
            1 -> {
                val newIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_flash_on, null)
                binding.flash.icon = newIcon
            }
            2 -> {
                val newIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_flash_auto, null)
                binding.flash.icon = newIcon
            }
        }
    }
}