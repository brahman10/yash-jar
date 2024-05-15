package com.jar.app.feature_sms_sync.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SmsSyncRequest(
    @SerialName("smsList")
    var smsList: List<SmsData>,
)