package com.jar.app.feature_contact_sync_common.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ContactListFeatureType {
    @SerialName("REFERRALS")
    REFERRALS,

    @SerialName("DUO")
    DUO,
}