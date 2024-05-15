package com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class SipTypeSelectionScreenData(
    val toolbarHeader: String,
    val shouldShowSelectionContainer: Boolean = true,
    val sipSubscriptionType: String
) : Parcelable