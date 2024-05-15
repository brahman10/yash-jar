package com.jar.app.feature_sms_sync.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SmsData(
    @SerialName("sender")
    var sender: String? = null,

    @SerialName("body")
    var body: String? = null,

    @SerialName("timestamp")
    var timestamp: Long? = null
)