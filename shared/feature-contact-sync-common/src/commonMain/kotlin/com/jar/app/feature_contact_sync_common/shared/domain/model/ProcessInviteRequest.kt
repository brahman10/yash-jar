package com.jar.app.feature_contact_sync_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ProcessInviteRequest (
    @SerialName("inviterId")
    var inviterId: String,

    @SerialName("invitationStage")
    val invitationStage: String)
