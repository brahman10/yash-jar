package com.jar.app.feature_contact_sync_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddContactRequest(
    @SerialName("userContacts")
    val userContacts: List<LocalContact>
)