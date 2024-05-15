package com.jar.app.feature_lending_kyc.shared.domain.arguments

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CreditReportScreenArguments(
    @SerialName("creditReportPan")
    val creditReportPan: CreditReportPAN?,
    @SerialName("jarVerifiedPAN")
    val jarVerifiedPAN: Boolean,
    @SerialName("panFlowType")
    val panFlowType: LendingKycConstants.PanFlowType,
    @SerialName("isBackNavOrViewOnlyFlow")
    val isBackNavOrViewOnlyFlow: Boolean,
    @SerialName("primaryAction")
    val primaryAction: PanErrorScreenPrimaryButtonAction,
    @SerialName("secondaryAction")
    val secondaryAction: PanErrorScreenSecondaryButtonAction,
    @SerialName("fromScreen")
    val fromScreen: String,
    @SerialName("description")
    val description: String,
    @SerialName("isPanAadhaarMismatch")
    val isPanAadhaarMismatch: Boolean = false,
    @SerialName("kycFeatureFlowType")
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
    @SerialName("lenderName")
    val lenderName: String? = null
) : Parcelable
