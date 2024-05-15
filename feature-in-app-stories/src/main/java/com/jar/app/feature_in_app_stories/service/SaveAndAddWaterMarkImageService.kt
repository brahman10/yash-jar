package com.jar.app.feature_in_app_stories.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jar.app.feature_in_app_stories.R
import com.jar.app.feature_in_app_stories.impl.uitl.WatermarkUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream

@AndroidEntryPoint
class SaveAndAddWaterMarkImageService : Service() {

    private val CHANNEL_ID: String = "waterMarkChannel"
    lateinit var watermarkUtil: WatermarkUtil
    private val TAG = "WatermarkService"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Display a notification to indicate that the service is running
        createNotificationChannel()

        // For devices running on a version below Android Oreo
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.in_app_story))
            .setContentText(getString(R.string.download_story))
            .setOngoing(true)
            .setProgress(100, 0, true)
            .build()
        startForeground(1, notification)


        val originalImageUrl = intent?.getStringExtra("originalImageUrl")
        val watermarkImageUrl = intent?.getStringExtra("watermarkImageUrl")

        NotificationCompat.Builder(this, CHANNEL_ID).setProgress(100, 50, false)
        // Call the injected WatermarkUtil to apply the watermark
        watermarkUtil.applyWatermarkToImages(
            originalImageUrl ?: "",
            watermarkImageUrl ?: ""
        ) { resultBitmap ->
            saveImageToGallery(resultBitmap)
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        watermarkUtil = WatermarkUtil(this)
    }

    private fun saveImageToGallery(bitmap: Bitmap): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Jar_story_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            val outputStream: OutputStream? = resolver.openOutputStream(uri)
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            MediaScannerConnection.scanFile(
                this,
                arrayOf(it.path),
                arrayOf("image/jpeg"),  // Mime types corresponding to the files
                null
            )
            Toast.makeText(this, "Image saved to your camera roll!", Toast.LENGTH_SHORT).show()
            val viewImageIntent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                viewImageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create and show the notification after saving the image
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Jar story")
                .setContentText("Your image has been saved to the gallery.")
                .setProgress(0, 0, false)  // remove the progress bar
                .setSmallIcon(R.drawable.jar_logo)
                .setContentIntent(pendingIntent)  // Add this line
                .setAutoCancel(true)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .build()

            val notificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(2, notification)  // use a different ID for this notification

            return uri.toString()
        }
        return null
    }
}
