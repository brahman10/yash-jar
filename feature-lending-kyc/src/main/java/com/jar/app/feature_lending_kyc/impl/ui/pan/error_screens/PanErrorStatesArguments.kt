package com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import kotlinx.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class PanErrorStatesArguments(
    val title: String,
    val description: String,
    val assetUrl: String,
    val primaryAction: PanErrorScreenPrimaryButtonAction,
    val secondaryAction: PanErrorScreenSecondaryButtonAction,
    val jarVerifiedPAN: Boolean? = null,
    val isLottie: Boolean = true,
    val contactMessage: String? = null,
    val fromScreen: String? = null,
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN
) : Parcelable
