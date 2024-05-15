package com.jar.app.base.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DailyInvestmentSetupArguments(
    val flowData:FeatureFlowData,
    val showIntroBottomSheet:Boolean = false,
    val fromAbandonFlow:Boolean = false
):Parcelable