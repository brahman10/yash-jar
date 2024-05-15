package com.jar.app.feature_sell_gold.shared.domain.models

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WithdrawRequest(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("apiResponse")
    val apiResponse: FetchCurrentGoldPriceResponse? = null,
    @SerialName("currentSellPrice")
    val currentSellPrice: Float? = null,
    @SerialName("instrumentType")
    val instrumentType: String? = null,
    @SerialName("newVpa")
    val newVpa: String? = null,
    @SerialName("savedVpaId")
    val savedVpaId: String? = null,
    @SerialName("rateId")
    val rateId: String? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("save")
    val save: Boolean? = null,
    @SerialName("volume")
    val volume: Float? = null,
    @SerialName("withDrawlType")
    val withDrawlType: String? = null,
    @SerialName("type")
    val type: String? = null
) : Parcelable