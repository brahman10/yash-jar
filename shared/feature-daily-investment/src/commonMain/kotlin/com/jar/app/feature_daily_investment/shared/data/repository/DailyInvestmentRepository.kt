package com.jar.app.feature_daily_investment.shared.data.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.domain.model.AbandonScreenBottomSheetResponse
import com.jar.app.feature_daily_investment.shared.domain.model.AmountSelectionResp
import com.jar.app.feature_daily_investment.shared.domain.model.DSOnboardingStoryData
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentBottomSheetV2Data
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentIntroData
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentMandateBottomSheetData
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentOnboardingFragmentData
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentOptionsResponse
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingEducationResp
import com.jar.app.feature_daily_investment.shared.domain.model.ExpandableFaqResponse
import com.jar.app.feature_daily_investment.shared.domain.model.UpdateDailyInvestmentStaticData
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentReward
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import kotlinx.coroutines.flow.Flow

interface DailyInvestmentRepository : BaseRepository {

    suspend fun fetchDailyInvestmentStatus(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>

    suspend fun updateDailyInvestmentStatus(
        amount: Float?,
        disable: Boolean?
    ): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>

    suspend fun fetchIsSavingPaused(
        savingsType: SavingsType,
        includeView: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>

    suspend fun fetchDailyInvestmentOptions(context: String?): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentOptionsResponse>>>

    suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType,
        customDuration: Long?
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>

    suspend fun fetchDSEducationData(): Flow<RestClientResult<ApiResponseWrapper<DailySavingEducationResp>>>

    suspend fun fetchAbandonBottomSheetData(
        contentType : String
    ): Flow<RestClientResult<ApiResponseWrapper<AbandonScreenBottomSheetResponse>>>

    suspend fun fetchAmountSelectionScreenData(): Flow<RestClientResult<ApiResponseWrapper<AmountSelectionResp?>>>

    suspend fun fetchDailySavingsFaqData(): Flow<RestClientResult<ApiResponseWrapper<ExpandableFaqResponse>>>

    suspend fun fetchDailyGoldInvestedSavings(): Flow<RestClientResult<ApiResponseWrapper<PostPaymentReward?>>>

    suspend fun fetchDailySavingsIntroBottomSheetData(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentIntroData>>>

    suspend fun fetchDailyInvestmentStoriesData(): Flow<RestClientResult<ApiResponseWrapper<DSOnboardingStoryData?>>>

    suspend fun fetchDailySavingsMandateBottomSheetData(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentMandateBottomSheetData?>>>

    suspend fun fetchDailyInvestmentBottomSheetV2Data(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentBottomSheetV2Data?>>>

    suspend fun fetchDailyInvestmentOnboardingData(
        version : String?
    ): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentOnboardingFragmentData?>>>

    suspend fun fetchUpdateDailyInvestmentStaticData(): Flow<RestClientResult<ApiResponseWrapper<UpdateDailyInvestmentStaticData?>>>

}