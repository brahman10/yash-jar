package com.jar.app.base.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast

fun isAndroidSDK13OrElse(positive: () -> Unit, negative: () -> Unit) {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
        positive.invoke()
    } else {
        negative.invoke()
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isAndroidSDK13() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun openPermissionSettings(message: String, context: Context) {
    try {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", context.packageName, null)
            }
        )
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

