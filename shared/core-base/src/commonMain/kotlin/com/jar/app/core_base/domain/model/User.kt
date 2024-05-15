package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@Parcelize
data class User(
    @SerialName("userId")
    var userId: String? = null,

    @SerialName("profilePicUrl")
    var profilePicUrl: String? = null,

    @SerialName("firstName")
    var firstName: String? = null,

    @SerialName("lastName")
    var lastName: String? = null,

    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("age")
    var age: Int? = null,

    @SerialName("gender")
    var gender: String? = null,

    @SerialName("email")
    var email: String? = null,

    @SerialName("userGoalSetup")
    var userGoalSetup: Boolean? = null,

    @SerialName("onboarded")
    var onboarded: Boolean? = null,

    @SerialName("createdAt")
    val createdAtInUtc: Long? = null

): Parcelable {

    fun getFullNameWithoutSpace() = firstName?.plus(lastName.orEmpty())
    fun getFullName() = firstName?.plus(" ")?.plus(lastName.orEmpty())

    fun getPhoneNumberWithoutPlus() = phoneNumber.replace("+", "")

    fun getPhoneNumberWithoutCountryCode() = phoneNumber.replace("+91", "")
}