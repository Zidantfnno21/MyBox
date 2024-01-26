package com.example.mybox.utils

import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mybox.R

class PermissionHandler(private val activity: Activity, private val requestCode: Int = REQUEST_CODE_PERMISSIONS) {

    @SuppressLint("InlinedApi")
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED, CAMERA, POST_NOTIFICATIONS)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, CAMERA, POST_NOTIFICATIONS)
    } else {
        arrayOf(READ_EXTERNAL_STORAGE, CAMERA, POST_NOTIFICATIONS)
    }

    fun requestPermissionsIfNecessary() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    fun onRequestPermissionsResult(requestCode : Int , permissions : Array<out String>) {
        if (requestCode == this.requestCode) {
            if (!allPermissionsGranted()) {
                if (shouldShowRequestPermissionRationale()) {
                    showPermissionDeniedDialog(permissions)
                }else{
                    showPermissionPermanentlyDeniedDialog()
                }

            }
        }
    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(activity.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale() = permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        val dialog = Dialog(activity, R.style.RoundShapeTheme)
        dialog.setContentView(R.layout.permission_custom_dialog)
        dialog.setCancelable(false)

        val title: TextView = dialog.findViewById(R.id.permissionTitle)
        title.text = activity.getString(R.string.permission_open_settings_title)
        val message: TextView = dialog.findViewById(R.id.permissionMessage)
        message.text = activity.getString(R.string.permission_open_settings_message)

        val retryButton: Button = dialog.findViewById(R.id.permissionRetry)
        retryButton.text = buildString {
        append("Open Settings")
    }
        retryButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package" , activity.packageName , null)
            intent.data = uri
            activity.startActivity(intent)
            dialog.dismiss()
        }

        val exitButton: Button = dialog.findViewById(R.id.permissionExit)
        exitButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPermissionDeniedDialog(permissions: Array<out String>) {
        val dialog = Dialog(activity, R.style.RoundShapeTheme)
        dialog.setContentView(R.layout.permission_custom_dialog)
        dialog.setCancelable(false)

        val retryButton: Button = dialog.findViewById(R.id.permissionRetry)
        retryButton.setOnClickListener {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
            dialog.dismiss()
        }

        val skipButton: Button = dialog.findViewById(R.id.permissionExit)
        skipButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

