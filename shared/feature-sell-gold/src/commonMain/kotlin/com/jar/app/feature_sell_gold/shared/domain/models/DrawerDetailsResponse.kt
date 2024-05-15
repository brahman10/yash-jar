package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrawerDetailsResponse(
    @SerialName("drawer")
    val drawer: Drawer,
    @SerialName("inputAmount")
    val inputAmount: InputAmount,
    @SerialName("faqText")
    val faqText: String,
    @SerialName("faqLink")
    val faqLink: String,
    @SerialName("withdrawalCards")
    val withdrawalCards: List<WithdrawalCard>
)

@Serializable
data class DrawerItem(
    @SerialName("iconLink")
    val iconLink: String,
    @SerialName("keyText")
    val keyText: String,
    @SerialName("amount")
    val amount: String,
    @SerialName("volume")
    val volume: String,
    @SerialName("unitPreference")
    val unitPreference: String,
    @SerialName("priority")
    val priority: Int? = null
)

@Serializable
data class Drawer(
    @SerialName("footerText")
    val footerText: String,
    @SerialName("drawerItems")
    val drawerItems: List<DrawerItem>
)

@Serializable
data class InputAmount(
    @SerialName("headerText")
    val headerText: String,
    @SerialName("footerText")
    val footerText: String,
    @SerialName("rupeeSymbol")
    val rupeeSymbol: String,
    @SerialName("minAmountError")
    val minAmountError: String,
    @SerialName("maxAmountError")
    val maxAmountError: String?,
    @SerialName("footerIconLink")
    val footerIconLink: String,
    @SerialName("errorIconLink")
    val errorIconLink: String? = null
)

@Serializable
data class WithdrawalCard(
    @SerialName("title")
    val title: String?, // always null in withdrawal cards
    @SerialName("description")
    val description: String,
    @SerialName("iconLink")
    val iconLink: String,
    @SerialName("backgroundColor")
    val backgroundColor: String
)