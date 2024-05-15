package com.jar.app.feature_daily_investment.impl.util

import com.jar.app.base.util.addPercentage
import com.jar.app.base.util.calculatePercentage
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DailySavingPredictionUtil @Inject constructor(
    private val buyGoldUseCase: BuyGoldUseCase
) {

    suspend fun generateDailySavingPrediction(
        months: Int,
        currentPrice: FetchCurrentGoldPriceResponse?,
        dailyInvestment: Float
    ): DailySavingPrediction {
        val totalNumberOfDays = months * DailySavingConstants.DAYS_IN_MONTH
        val totalInvestment = (dailyInvestment * totalNumberOfDays)
        val totalInvestmentAfterTax =
            (dailyInvestment * totalNumberOfDays).calculatePercentage(100 - currentPrice?.applicableTax.orZero())
        val totalInvestmentAfterAppreciation = totalInvestmentAfterTax.addPercentage(14f * months/12)
        return DailySavingPrediction(
            totalInvestment = totalInvestment,
            totalInvestmentAfterTax = totalInvestmentAfterTax,
            totalInvestmentAfterAppreciation = totalInvestmentAfterAppreciation,
            totalInvestmentInVolume = buyGoldUseCase.calculateVolumeFromAmountSync(
                amount = totalInvestmentAfterAppreciation,
                fetchCurrentGoldPriceResponse = currentPrice
            ).data.orZero(),
            gstOnTotalAmountSaved = totalInvestment.calculatePercentage(currentPrice?.applicableTax.orZero())
        )
    }
}

data class DailySavingPrediction(
    val totalInvestment: Float,
    val totalInvestmentAfterTax: Float,
    val totalInvestmentAfterAppreciation: Float,
    val totalInvestmentInVolume: Float,
    val gstOnTotalAmountSaved: Float
)