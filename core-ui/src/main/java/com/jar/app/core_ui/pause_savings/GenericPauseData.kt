package com.jar.app.core_ui.pause_savings
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper
import kotlinx.serialization.Serializable

@Serializable
data class GenericPauseData(
    val list: ArrayList<PauseSavingOptionWrapper>
)