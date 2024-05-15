package com.jar.app.feature_contact_sync_common.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultipleInviteRequest(
    @SerialName("inviteePhoneNumberList")
    val inviteePhoneNumberList: List<String>,

    @SerialName("searchText")
    val searchText: String? = null,

    @SerialName("selectAllEnabled")
    val selectAllEnabled: Boolean,

    @SerialName("referralLink")
    val referralLink: String?
)
