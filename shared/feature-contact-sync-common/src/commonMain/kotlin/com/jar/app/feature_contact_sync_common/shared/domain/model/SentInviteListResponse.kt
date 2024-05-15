package com.jar.app.feature_contact_sync_common.shared.domain.model


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SentInviteListResponse(
    @SerialName("duoPendingInvitesSentObjects")
    val contactsSyncPendingInvitesSentObjects: List<ContactsSyncPendingInvitesSentObject>,
    @SerialName("totalInvitesSent")
    val totalInvitesSent: Int,
    @SerialName("totalInvitesSentForJarUsers")
    val totalInvitesSentForJarUsers: Int
)