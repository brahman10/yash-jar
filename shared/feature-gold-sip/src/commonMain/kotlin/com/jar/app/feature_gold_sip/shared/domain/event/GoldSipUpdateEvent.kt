package com.jar.app.feature_gold_sip.shared.domain.event

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
data class GoldSipUpdateEvent(
    val sipAmount: Float,
    val sipDay: String,
    val sipDayValue: Int,
    val subscriptionType: String
) : Parcelable
