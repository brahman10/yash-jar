package com.jar.app.feature_sms_sync.api

//Exposed to outer world to trigger the SMS sync flow
interface SmsSyncApi {
    fun startMessageSync(numberOfDaysOfSms: Int, skipLastSyncCheck: Boolean)

    fun onUserLogout()
}