package com.jar.app.feature_contact_sync_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ServerContactHeader(
    @SerialName("header")
    val header: String,

    override val uniqueId: String = header
) : ServerContactList

object AllowAccessData : ServerContactList {
    override val uniqueId: String
        get() = "AllowAccessData"
}