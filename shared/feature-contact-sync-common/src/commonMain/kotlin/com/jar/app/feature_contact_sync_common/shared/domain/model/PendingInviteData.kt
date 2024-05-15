package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PendingInviteData(
    @SerialName("name")
    val name: String? = null,

    @SerialName("phoneNumber")
    val phoneNumber: String? = null,

    @SerialName("inviterId")
    val inviterId: String? = null,

    @SerialName("profilePicture")
    val profilePicture: String? = null,

    @SerialName("userOnboardedTime")
    val userOnboardedTime: Long? = null,
) : Parcelable