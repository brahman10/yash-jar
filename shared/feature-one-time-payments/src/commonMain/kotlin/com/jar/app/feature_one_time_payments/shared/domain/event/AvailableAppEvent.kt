package com.jar.app.feature_one_time_payments.shared.domain.event

import kotlinx.serialization.json.JsonArray

data class AvailableAppEvent(
    val upiApps: JsonArray
)