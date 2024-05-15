package com.jar.app.feature_post_setup.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPostSetupData(
    @SerialName("noOfDaysActive")
    val noOfDaysActive: Int? = null,
    @SerialName("enabled")
    val enabled: Boolean? = null,
    @SerialName("newUser")
    val newUser: Boolean? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("footer")
    val footer: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("headerOrder")
    val headerOrder: Int? = null,
    @SerialName("calendarOrder")
    val calendarOrder: Int? = null,
    @SerialName("amountInfoOrder")
    val amountInfoOrder: Int? = null,
    @SerialName("quickActionOrder")
    val quickActionOrder: Int? = null,
    @SerialName("faqOrder")
    val faqOrder: Int? = null,
    @SerialName("footerOrder")
    val footerOrder: Int? = null,
    @SerialName("startDate")
    val startDate: Long? = null,
    @SerialName("endDate")
    val endDate: Long? = null,
    @SerialName("spinsCount")
    val spinsCount: Int? = null,
    @SerialName("totalAmount")
    val totalAmount: Float? = null,
    @SerialName("dailySavingsInfo")
    val postSetupDailySavingsInfo: PostSetupDailySavingsInfo? = null
)
