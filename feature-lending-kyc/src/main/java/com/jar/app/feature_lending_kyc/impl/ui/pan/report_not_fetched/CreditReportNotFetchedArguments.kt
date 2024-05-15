package com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import kotlinx.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class CreditReportNotFetchedArguments(
    val title: String,
    val description: String,
    val assetUrl: String,
    val primaryAction: PanErrorScreenPrimaryButtonAction,
    val secondaryAction: PanErrorScreenSecondaryButtonAction,
    val fromScreen: String,
    val jarVerifiedPAN: Boolean,
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN
) : Parcelable