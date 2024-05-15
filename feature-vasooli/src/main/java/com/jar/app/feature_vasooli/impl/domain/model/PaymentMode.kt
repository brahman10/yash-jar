package com.jar.app.feature_vasooli.impl.domain.model

import androidx.annotation.StringRes

data class PaymentMode(
    val id: Int,
    @StringRes
    val title: Int,
    val isSelected: Boolean = false
)