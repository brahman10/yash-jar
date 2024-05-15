package com.jar.app.feature_mandate_payment_common.impl.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpiAppsUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManagerUtil: PackageManagerUtil
) {

    fun getMandateReadyUpiAppsName(): List<String> {
        val upiList = ArrayList<String>()
        val uri = Uri.parse(String.format("%s://%s", "upi", "mandate"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
            context.packageManager.queryIntentActivities(
                upiUriIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        val upiPackageNameList = ArrayList<String>()
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            upiPackageNameList.add(packageName)
            upiList.add(packageManagerUtil.getAppNameFromPkgName(packageName) ?: packageName)
        }
        return upiList
    }

    fun getMandateReadyUpiAppsPackageName(): List<String> {
        val uri = Uri.parse(String.format("%s://%s", "upi", "mandate"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
            context.packageManager.queryIntentActivities(
                upiUriIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        val upiPackageNameList = ArrayList<String>()
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            upiPackageNameList.add(packageName)
        }
        return upiPackageNameList
    }
}