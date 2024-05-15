package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.pan_preview

import android.os.Parcelable
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class IdentityConfirmationScreenArguments(
    @SerialName("creditReportPan")
    val creditReportPan: CreditReportPAN? = null
) : Parcelable
