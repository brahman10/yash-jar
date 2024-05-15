package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse

@Parcelize
@kotlinx.serialization.Serializable
data class ServerContactResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: ContactListResponse?
): Parcelable