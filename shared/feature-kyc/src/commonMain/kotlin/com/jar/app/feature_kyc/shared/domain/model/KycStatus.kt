package com.jar.app.feature_kyc.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class KycStatus(
    val title: String? = null,
    val description: String? = null,
    val shareMsg: String? = null,
    val verificationStatus: String? = null,
    val shouldTryAgain: Boolean? = null,
    val allRetryExhausted: Boolean? = null,
    val isFromFlow: Boolean = true
): Parcelable {
    fun getLottieForStatus(): String? {
        return when (verificationStatus) {
            KycVerificationStatus.VERIFIED.name -> "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
            KycVerificationStatus.FAILED.name-> "${BaseConstants.CDN_BASE_URL}/LottieFiles/KYC/failed.json"
            KycVerificationStatus.PENDING.name -> "${BaseConstants.CDN_BASE_URL}/LottieFiles/KYC/verifying.json"
            KycVerificationStatus.RETRY.name -> "${BaseConstants.CDN_BASE_URL}/LottieFiles/KYC/failed.json"
            else -> null
        }
    }
}