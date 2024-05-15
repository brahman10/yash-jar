package com.jar.app.feature_daily_investment.shared.domain.model

import com.jar.app.feature_user_api.domain.model.PauseSavingOption

data class PauseDailySavingData(
    val pauseDailySavingsOption: PauseSavingOption,
    var isSelected: Boolean = false
)