package com.jar.app.feature_user_api.domain.model

data class UserMetaData(
    val id: Long,

    var referAndEarnDescription: String? = null,

    var referralEarnings: Double? = null,

    val notificationCount: Long? = null,

    val popupType: String? = null,

    val pendingGoldGift: Boolean? = null,

    val creditCardShow: Boolean? = null,

    val shouldShowLoanCard: Boolean? = null,

    val showVasooliCard : Boolean? = null,

    val showDuoCard : Boolean? = null
)