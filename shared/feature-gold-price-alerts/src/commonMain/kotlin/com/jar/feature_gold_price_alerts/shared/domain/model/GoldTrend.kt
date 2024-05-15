package com.jar.feature_gold_price_alerts.shared.domain.model

import com.jar.app.core_base.util.orZero
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldTrendBottomSheetStaticData(
    @SerialName("setPriceAlertCta")
    val saveGoldCta: SaveGoldCta? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("footerIconUrl")
    val footerIconUrl: String? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("liveBuyPrice")
    val liveBuyPrice: LiveBuyPrice,
    @SerialName("liveGoldPriceText")
    val liveGoldPriceText: String? = null,
    @SerialName("priceDropPills")
    val priceDropPills: List<Double>? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("pricePills")
    val pricePills: List<GoldTrendPricePill>? = null
)

@kotlinx.serialization.Serializable
data class GoldTrendScreenStaticData(
    @SerialName("alertCta")
    val saveGoldCta: SaveGoldCta? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("subText1")
    val subText1: String? = null,
    @SerialName("activeAlertExists")
    val activeAlertExists: Boolean? = null,
    @SerialName("liveBuyPrice")
    val liveBuyPrice: LiveBuyPrice? = null,
    @SerialName("savingsCard")
    val savingsCard: SavingsCard? = null,
    @SerialName("savingsCardV3")
    val savingsCardV3: SavingsCard? = null,
    @SerialName("timeframes")
    val timeframes: List<Timeframe>? = null,
    @SerialName("topRibbon")
    val topRibbon: GoldTrendTopRibbon? = null,
    @SerialName("toolbarTitle")
    val toolbarTitle: String? = null,
    @SerialName("liveBuyPriceTitle")
    val liveBuyPriceTitle: String? = null,
    @SerialName("alertStatus")
    private val alertStatus: String? = null
) {
    fun getAlertStatus() = GoldTrendAlertStatus.values().find { it.name == alertStatus }
}

@kotlinx.serialization.Serializable
data class GoldTrend(
    @SerialName("karat")
    val karat: String? = null,
    @SerialName("minAmount")
    val minAmount: Double? = null,
    @SerialName("period")
    val period: Int? = null,
    @SerialName("purity")
    val purity: String? = null,
    @SerialName("subText1")
    val subText: String? = null,
    @SerialName("tableData")
    val tableData: List<TableData?>? = null,
    @SerialName("unit")
    val unit: String? = null,
    @SerialName("xaxis")
    val xaxis: List<String>,
    @SerialName("yaxis")
    val yaxis: List<String>,
    @SerialName("trendsStartText")
    val trendsStartText: String? = null,
    @SerialName("trendsEndText")
    val trendsEndText: String? = null,
) {

    fun getPercentageChange(): Double {
        val first = yaxis?.first()?.toDouble().orZero()//Latest price
        val last = yaxis?.last()?.toDouble().orZero() //Oldest Price
        return ((first - last) / first) * 100
    }
}

enum class GoldTrendAlertStatus {
    NOT_SET,
    ALERT_ACTIVE,
    PRICE_REACHED
}

enum class PeriodUnit(unit: String) {
    MONTHS("months"),
    YEARS("years")
}