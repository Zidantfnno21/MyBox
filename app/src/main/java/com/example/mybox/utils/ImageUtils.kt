package com.example.mybox.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.example.mybox.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateFile(file: File, isBackCamera: Boolean = false) {
    val matrix = Matrix()
    val bitmap = BitmapFactory.decodeFile(file.path)
    val rotation = if (isBackCamera) 90f else -90f
    matrix.postRotate(rotation)
    if (!isBackCamera) {
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }
    val result = Bitmap.createBitmap(bitmap , 0 , 0 , bitmap.width , bitmap.height , matrix , true)
    result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
}

fun reduceFileImage(uri: Uri, contentResolver: ContentResolver): Uri? {
    try {
        // Decode bitmap from content URI using ContentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        var compressQuality = 100
        var streamLength: Int
        val maxFileSize = 1000000

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > maxFileSize && compressQuality > 0)

        // Create a temporary file and write compressed bitmap to the file
        val tempFile = File.createTempFile("compressed_file", ".jpg")
        val fileOutputStream = FileOutputStream(tempFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, fileOutputStream)
        fileOutputStream.close()

        // Return URI for the compressed image file
        return Uri.fromFile(tempFile)
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle exceptions or errors appropriately
        return null
    }
}