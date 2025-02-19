package com.example.mybox.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mybox.data.repository.BoxRepository

class SyncCategoryWorker(
    private val repository: BoxRepository,
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncUnsyncedCategories()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncCategoryWorker", "Sync failed: ${e.message}")
            Result.retry()
        }
    }
}