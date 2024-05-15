package com.jar.app.base.util.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

class DownloadHelper(context: Context) : Downloader {

    private val downloadManager =
        ContextCompat.getSystemService(context, DownloadManager::class.java)

    override fun downloadVideoFile(url: String): Long? {
        val videoFileName = url.substringAfterLast("/")
        val defaultVideoFileName = "jar-stories"
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("video/mp4")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Jar Stories")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${videoFileName.takeIf { !videoFileName.isNullOrEmpty() } ?: defaultVideoFileName}.mp4"
            )
        return downloadManager?.enqueue(request)
    }
}