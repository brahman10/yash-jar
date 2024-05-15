package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PendingInviteResponse(
    @SerialName("duoPendingInvitesSentObjects")
    val list: List<PendingInviteData>? = null,
    @SerialName("totalInvitesSent")
    val totalInvitesSent: Int? = null,
    @SerialName("totalInvitesSentForJarUsers")
    val totalInvitesSentForJarUsers: Int? = null
) : Parcelable