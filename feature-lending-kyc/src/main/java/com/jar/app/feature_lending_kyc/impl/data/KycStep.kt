package com.jar.app.feature_lending_kyc.impl.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.jar.app.feature_lending_kyc.R

data class KycStep(
    val status: KycStepStatus,
    val text: String,
    val position: Int = 0,
    val stepProgress: Int = 100
) {
    fun isPending() = status == KycStepStatus.NOT_YET_VISITED
}

enum class KycStepStatus(@StringRes val statusText: Int, @ColorRes val statusTextColor: Int) {
    FAILURE(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_in_progress.resourceId, com.jar.app.core_ui.R.color.color_EBB46A),
    NOT_YET_VISITED(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_empty.resourceId, com.jar.app.core_ui.R.color.transparent),
    IN_PROGRESS(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_in_progress.resourceId, com.jar.app.core_ui.R.color.color_EBB46A),
    COMPLETED(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_completed.resourceId, com.jar.app.core_ui.R.color.commonTxtColor)
}

enum class Step(@StringRes val titleRes: Int, val stepNumber: Int) {
    EMAIL(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_email_verification.resourceId, 1),
    PAN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_verification.resourceId, 2),
    AADHAAR(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_verification.resourceId, 3),
    SELFIE(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_selfie_verification.resourceId, 4)
}