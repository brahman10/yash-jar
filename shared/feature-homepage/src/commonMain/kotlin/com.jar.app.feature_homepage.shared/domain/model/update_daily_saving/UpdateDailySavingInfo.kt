package com.jar.app.feature_homepage.shared.domain.model.update_daily_saving

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateDailySavingInfo(

    @SerialName("updateDailySavingsInfo")
    val updateDailySavingData: UpdateDailySavingData? = null
)