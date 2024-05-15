package com.jar.app.feature_daily_investment.impl.domain.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntermediateTransitionScreenArgs(
    @DrawableRes val illustrationRes:Int,
    val title:String,
    val subtitle:String,
    val shouldShowProgress:Boolean = true,
    val shouldShowConfettiAnimation:Boolean = false
):Parcelable
