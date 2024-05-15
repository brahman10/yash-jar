package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SellGoldStaticData(
    @SerialName("sellGoldWithDrawReasons")
    val withdrawReasons: List<String>? = null,

    @SerialName("helpWorkFlow")
    val helpWorkFlow: HelpWorkFlow? = null,

    @SerialName("sellGoldPercentage")
    val sellGoldPercentage: List<SellGoldPercentage>? = null
) {
    fun getWithdrawReasonList(): List<WithdrawReason> {
        return withdrawReasons?.map {
            WithdrawReason(
                it
            )
        } ?: emptyList()
    }
}