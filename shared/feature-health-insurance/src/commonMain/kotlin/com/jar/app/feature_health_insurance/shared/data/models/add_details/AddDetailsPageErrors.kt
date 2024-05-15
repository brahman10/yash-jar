package com.jar.app.feature_health_insurance.shared.data.models.add_details

enum class AddDetailsPageErrors(val error: String) {
    FE_MAX_AGE_ERROR("maximumAgeError"),
    FE_MIN_AGE_ERROR("minimumAgeError"),
    FE_MAX_AGE_SPOUSE_ERROR("maximumMyselfAndSpouseError")
}