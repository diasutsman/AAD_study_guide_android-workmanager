package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.TAG_OUTPUT

class BlurWorker(ctx: Context, workerParams: WorkerParameters) : Worker(ctx, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        sleep()

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG_OUTPUT, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val picture =
                BitmapFactory.decodeStream(appContext.contentResolver.openInputStream(
                    Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)
            // write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            return Result.success(outputData)
        } catch (e: Throwable) {
            Log.e(TAG_OUTPUT, "Error applying blur")
            e.printStackTrace()
            return Result.failure()
        }
    }
}