package com.jar.app.feature_transaction.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class InvestmentBreakDown(
    @SerialName("keys")
    val keys: List<String>,
    @SerialName("values")
    val values: List<Float>,
    @SerialName("extraGoldBreakupObject")
    val extraGoldBreakupObject: ExtraGoldBreakupObject,
    @SerialName("investedValue")
    val investedValue: Float
)

@Serializable
data class ExtraGoldBreakupObject(
    @SerialName("keys")
    val keys: List<String>,
    @SerialName("values")
    val values: List<Float>,
    @SerialName("investedExtraGold")
    val investedExtraGold: Float,
    @SerialName("extraGoldKey")
    val extraGoldKey: String
)