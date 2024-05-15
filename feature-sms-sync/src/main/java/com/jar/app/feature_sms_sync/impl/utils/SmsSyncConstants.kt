package com.jar.app.feature_sms_sync.impl.utils

internal object SmsSyncConstants {

    const val PREFERENCES_FILE_NAME = "sms_sync"
    const val PREF_KEY_LAST_SYNC_TIME = "last_sync_time_stamp"

    const val Endpoint = "smsingestion"
    const val SMS_PARSER_HOST_URL = "webhook.myjar.app"

    const val NON_PERSONAL_SENDER_LENGTH = 9

    object EventKey {
        const val SMS_SYNC = "SMS_SYNC_NEW"

        const val EVENT_TYPE_KEY = "event_type"

        const val EVENT_TYPE_STARTED = "started"
        const val EVENT_TYPE_SUCCESS = "success"
        const val EVENT_TYPE_FAILURE = "failure"
        const val FAILURE_REASON = "reason"
    }
}