package com.jar.app.feature_lending_kyc.shared.domain.model

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
data class AadhaarActionPromptArgs(
    val assetsUrl: String,
    val titleText: String,
    val subtitleText: String,
    val primaryActionText: String,
    val secondaryActionText: String,
    val primaryButtonAction: AadhaarErrorScreenPrimaryButtonAction,
    val secondaryButtonAction: AadhaarErrorScreenSecondaryButtonAction,
    val isIllustrationUrl: Boolean = false,
    val isPanAadhaarMismatch: Boolean = false,
    val contactMessage: String? = null,
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN
) : Parcelable