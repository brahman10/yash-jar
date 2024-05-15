package com.jar.app.feature_mandate_payment_common.impl.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageManagerUtil @Inject constructor(@ApplicationContext context: Context) {

    private val packageManager: PackageManager = context.packageManager

    fun getAppIconFromPkgName(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getAppNameFromPkgName(packageName: String): String? {
        return try {
            val info =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationLabel(info) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }
}