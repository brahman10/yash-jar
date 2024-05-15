package com.jar.app.feature_onboarding.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GenderData(
    @SerialName("gender")
    val genderStringId: Int,

    @SerialName("type")
    val genderType: com.jar.app.feature_onboarding.shared.domain.model.GenderType,

    @SerialName("isSelected")
    var isSelected: Boolean = false
)

enum class GenderType {
    MALE, FEMALE, OTHER
}