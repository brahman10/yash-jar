package com.jar.app.feature_contact_sync_common.shared.domain.model


import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ContactsSyncPendingInvitesSentObject(
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("inviteeId")
    val inviteeId: String? = null,
    @SerialName("jarUser")
    val jarUser: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("profilePicture")
    val profilePicture: String? = null,
    @SerialName("remindedAt")
    val remindedAt: Long
) : com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList,Parcelable{

}