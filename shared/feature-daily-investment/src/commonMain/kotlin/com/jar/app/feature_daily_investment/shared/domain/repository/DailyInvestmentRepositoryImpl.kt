package com.jar.app.feature_daily_investment.shared.domain.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.data.network.DailyInvestmentDataSource
import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType

internal class DailyInvestmentRepositoryImpl constructor(
    private val dailyInvestmentDataSource: DailyInvestmentDataSource
) : DailyInvestmentRepository {

    override suspend fun fetchDailyInvestmentStatus(includeView: Boolean) =
        getFlowResult { dailyInvestmentDataSource.fetchDailyInvestmentStatus(includeView) }

    override suspend fun updateDailyInvestmentStatus(amount: Float?, disable: Boolean?) =
        getFlowResult {
            dailyInvestmentDataSource.updateDailyInvestmentStatus(amount, disable)
        }

    override suspend fun fetchDailyInvestmentOptions(context: String?) = getFlowResult {
        dailyInvestmentDataSource.fetchDailyInvestmentOptions(context)
    }

    override suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType,
        customDuration: Long?
    ) = getFlowResult {
        dailyInvestmentDataSource.updateSavingPauseDuration(
            pause,
            pauseDuration,
            savingType,
            customDuration
        )
    }

    override suspend fun fetchDailyGoldInvestedSavings() = getFlowResult {
            dailyInvestmentDataSource.fetchAutoGoldInvestedSavings()
        }

    override suspend fun fetchDSEducationData() = getFlowResult {
        dailyInvestmentDataSource.fetchDSEducationData()
    }

    override suspend fun fetchAbandonBottomSheetData(
        contentType : String
    ) = getFlowResult {
        dailyInvestmentDataSource.fetchAbandonBottomSheetData(contentType)
    }

    override suspend fun fetchAmountSelectionScreenData() = getFlowResult {
        dailyInvestmentDataSource.fetchAmountSelectionScreenData()
    }

    override suspend fun fetchDailySavingsFaqData() = getFlowResult {
        dailyInvestmentDataSource.fetchDailySavingsFaqData()
    }


    override suspend fun fetchIsSavingPaused(
        savingsType: SavingsType,
        includeView: Boolean
    ) = getFlowResult {
        dailyInvestmentDataSource.areSavingPaused(savingsType, includeView)
    }

    override suspend fun fetchDailySavingsIntroBottomSheetData() = getFlowResult {
            dailyInvestmentDataSource.fetchDailySavingsIntroBottomSheetData()
        }

    override suspend fun fetchDailySavingsMandateBottomSheetData() = getFlowResult {
        dailyInvestmentDataSource.fetchDailySavingsMandate()
    }

    override suspend fun fetchDailyInvestmentBottomSheetV2Data() = getFlowResult {
        dailyInvestmentDataSource.fetchDailyInvestmentBottomSheetV2Data()
    }

    override suspend fun fetchDailyInvestmentStoriesData() = getFlowResult {
        dailyInvestmentDataSource.fetchDailyInvestmentStoriesData()
    }

    override suspend fun fetchDailyInvestmentOnboardingData(version: String?) = getFlowResult {
        dailyInvestmentDataSource.fetchDailyInvestmentOnboardingFragmentData(version)
    }

    override suspend fun fetchUpdateDailyInvestmentStaticData() = getFlowResult {
        dailyInvestmentDataSource.fetchUpdateDailyInvestmentStaticData()
    }

}