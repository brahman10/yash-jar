package com.jar.app.feature_vasooli.impl.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Reminder(
    @SerialName("imageId")
    val imageId: String,

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("reminderText")
    val reminderText: String? = null,

    @SerialName("showShareTray")
    val showShareTray: Boolean? = null
): Parcelable