package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PauseSavingOptionWrapper(
    val pauseSavingOption: PauseSavingOption,
    var isSelected: Boolean = false
)