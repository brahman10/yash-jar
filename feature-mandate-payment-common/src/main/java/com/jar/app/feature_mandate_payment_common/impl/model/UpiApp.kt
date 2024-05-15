package com.jar.app.feature_mandate_payment_common.impl.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpiApp(
    @SerialName("packageName")
    val packageName: String,

    @SerialName("icon")
    @kotlinx.serialization.Transient
    val icon: Drawable? = null,

    @SerialName("appName")
    val appName: String
)