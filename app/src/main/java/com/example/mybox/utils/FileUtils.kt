package com.example.mybox.utils

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileUtils @Inject constructor(@ApplicationContext val context: Context) {

    fun createAndWriteToCache(fileName: String, data: String, isSuccess: (Boolean) -> Unit) {
        val cacheDir: File = context.cacheDir
        val myFile = File(cacheDir, fileName.replace("/", "_"))

        try {
            BufferedOutputStream(FileOutputStream(myFile)).use { bos ->
                bos.write(data.toByteArray())
                isSuccess(true)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            isSuccess(false)
        }
    }

    suspend fun getCached(fileName: String): File? = withContext(Dispatchers.IO) {
        val cacheDir: File = context.cacheDir
        val localFile = File(cacheDir, fileName.replace("/", "_"))

        return@withContext if (localFile.exists()) {
            localFile
        } else {
            Log.e("FileUtils", "File not found in cache: $fileName")
            null
        }
    }
}