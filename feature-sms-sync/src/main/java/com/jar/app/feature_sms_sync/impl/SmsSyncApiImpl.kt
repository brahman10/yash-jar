package com.jar.app.feature_sms_sync.impl

import android.content.SharedPreferences
import com.jar.app.feature_sms_sync.api.SmsSyncApi
import com.jar.app.feature_sms_sync.impl.di.SmsSyncPreferences
import com.jar.app.feature_sms_sync.impl.utils.SmsSyncUtils
import javax.inject.Inject

internal class SmsSyncApiImpl @Inject constructor(
    private val smsSyncUtils: SmsSyncUtils,
    @SmsSyncPreferences private val pref: SharedPreferences,
) : SmsSyncApi {
    override fun startMessageSync(numberOfDaysOfSms: Int, skipLastSyncCheck: Boolean) {
        smsSyncUtils.starSync(numberOfDaysOfSms, skipLastSyncCheck)
    }

    override fun onUserLogout() {
        pref.edit().clear().apply()
    }
}