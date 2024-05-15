package com.jar.app.feature_profile.impl.model

import androidx.annotation.DrawableRes
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.domain.model.GenderData
import com.jar.app.feature_profile.domain.model.GenderType

@DrawableRes
fun GenderData.getDrawableForGender(): Int {
    return when (this.genderType) {
        GenderType.MALE -> R.drawable.feature_profile_ic_male_new
        GenderType.FEMALE -> R.drawable.feature_profile_ic_female_new
        GenderType.OTHER -> R.drawable.feature_profile_ic_other_smiley
    }
}