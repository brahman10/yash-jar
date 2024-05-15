package com.jar.app.feature_contact_sync_common.shared.domain.model

import com.jar.app.core_base.util.orFalse
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ServerContact(
    @SerialName("id")
    val id: String,

    @SerialName("friendCountryCode")
    val friendCountryCode: String,

    @SerialName("friendPhoneNumber")
    val friendPhoneNumber: String,

    @SerialName("friendName")
    val friendName: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("friendUserId")
    val friendUserId: String? = null,

    @SerialName("invitedForDuo")
    var invitedForDuo: Boolean,

    @SerialName("friendJarUser")
    val friendJarUser: Boolean,

    @SerialName("isSelected")
    var isSelected: Boolean? = null,

    @SerialName("profilePicture")
    val profilePicture: String? = null,

    @SerialName("userOnboardedTime")
    val userOnboardedTime: Long? = null,

    override val uniqueId: String = id.plus(isSelected.orFalse())
) : ServerContactList, Parcelable {

    fun getNumberWithPlusSignAndCountryCode(): String {
        val plusSign = "+"
        return plusSign.plus(friendCountryCode).plus(friendPhoneNumber)
    }

    fun getNumberWithPlusSignAndCountryCodeFormatted(): String {
        val plusSign = "+"
        return plusSign.plus(friendCountryCode).plus(" ").plus(friendPhoneNumber)
    }

    fun hasNoInitial(): Boolean{
        return this.friendName.startsWith("+") || this.friendName.getOrNull(0)?.isLetter() == false
    }
}