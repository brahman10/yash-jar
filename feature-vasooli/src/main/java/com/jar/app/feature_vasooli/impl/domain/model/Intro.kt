package com.jar.app.feature_vasooli.impl.domain.model

import androidx.annotation.StringRes

data class Intro(
    @StringRes
    val title: Int?,

    @StringRes
    val description: Int?,

    val imageLink: String
)