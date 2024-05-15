package com.jar.app.feature_gold_sip.shared.domain.model

import dev.icerock.moko.resources.desc.ResourceStringDesc

data class WeekOrMonthData(
    val text: ResourceStringDesc?,
    val value: Int,
    var isSelected: Boolean = false
)