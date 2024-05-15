package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ContactListText(
    @SerialName("buttonText")
    val buttonText: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("title")
    val title: String? = null,
) : Parcelable