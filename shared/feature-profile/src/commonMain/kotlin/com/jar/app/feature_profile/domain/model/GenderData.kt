package com.jar.app.feature_profile.domain.model

import dev.icerock.moko.resources.StringResource

data class GenderData(
    val genderStringId: StringResource,
    val genderType: GenderType,

    //For UI purpose
    var isSelected: Boolean = false
)

enum class GenderType {
    MALE, FEMALE, OTHER
}