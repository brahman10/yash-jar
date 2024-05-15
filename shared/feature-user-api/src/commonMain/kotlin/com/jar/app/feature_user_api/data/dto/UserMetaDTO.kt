package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserMetaDTO(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("referEarn")
    var referAndEarnDescription: String? = null,

    @SerialName("referralEarnings")
    var referralEarnings: Double? = null,

    @SerialName("notificationCount")
    val notificationCount: Long? = null,

    @SerialName("popup")
    val popupType: String? = null,

    @SerialName("pendingGoldGift")
    val pendingGoldGift: Boolean? = null,

    @SerialName("creditCardShow")
    val creditCardShow: Boolean? = null,

    @SerialName("shouldShowLoanCard")
    val shouldShowLoanCard: Boolean? = null,

    @SerialName("showVasooliCard")
    val showVasooliCard : Boolean? = null,

    @SerialName("showDuoCard")
    val showDuoCard : Boolean? = null
)
