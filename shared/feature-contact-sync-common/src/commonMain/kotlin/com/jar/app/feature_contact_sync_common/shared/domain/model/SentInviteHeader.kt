package com.jar.app.feature_contact_sync_common.shared.domain.model

import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SentInviteHeader(
    @SerialName("header")
    val header: String,
) : SentInviteList