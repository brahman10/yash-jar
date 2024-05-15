package com.jar.app.feature_lending_kyc.impl.ui.loading

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenericLoadingArguments(
    val title: String,
    val description: String?,
    val assetsUrl: String?,
    val illustrationResourceId: Int?,
    val shouldShowPoweredBy: Boolean = false,
    val shouldShowWarningMessage: Boolean = false,
    val isIllustrationUrl: Boolean = false,
) : Parcelable