package com.jar.app.feature_daily_investment.shared.data.network

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
import com.jar.app.feature_daily_investment.shared.util.Constants.Endpoints
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentReward
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class DailyInvestmentDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchDailyInvestmentStatus(includeView: Boolean) =
        getResult<ApiResponseWrapper<DailyInvestmentStatus>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_STATUS)
                parameter("includeView", includeView)
            }
        }

    suspend fun updateDailyInvestmentStatus(amount: Float?, disable: Boolean?) =
        getResult<ApiResponseWrapper<DailyInvestmentStatus?>> {
            client.get {
                url(Endpoints.UPDATE_DAILY_INVESTMENT_STATUS)
                if (amount != null) parameter("amount", amount)
                if (disable != null) parameter("disable", disable)
            }
        }

    suspend fun fetchDailyInvestmentBottomSheetV2Data() =
        getResult<ApiResponseWrapper<DailyInvestmentBottomSheetV2Data?>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_INTRO_BOTTOM_SHEET_DATA)
                parameter("contentType", "DS_SETUP_BOTTOM_SHEET")
            }
        }

    suspend fun fetchDailyInvestmentOptions(context: String?) =
        getResult<ApiResponseWrapper<DailyInvestmentOptionsResponse>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_OPTIONS)
                parameter("contentType", "RECURRINGSAVINGS_INFO")
                parameter("context", context)
            }
        }

    suspend fun updateSavingPauseDuration(
        pause: Boolean, pauseDuration: String?, savingType: SavingsType, customDuration: Long?
    ) = getResult<ApiResponseWrapper<PauseSavingResponse>> {
        client.get {
            url(Endpoints.UPDATE_PAUSE_SAVING_DURATION)
            parameter("pause", pause)
            parameter("pauseType", savingType.name)
            if (pauseDuration.isNullOrBlank().not()) parameter("pauseDuration", pauseDuration)
            if (customDuration != null) parameter("customDuration", customDuration)
        }
    }

    suspend fun areSavingPaused(
        savingsType: SavingsType, includeView: Boolean
    ) = getResult<ApiResponseWrapper<PauseSavingResponse>> {
        client.get {
            url(Endpoints.ARE_SAVINGS_PAUSED)
            parameter("pauseType", savingsType.name)
            parameter("includeView", includeView)
        }
    }

    suspend fun fetchDSEducationData(
    ) = getResult<ApiResponseWrapper<DailySavingEducationResp>> {
        client.get {
            url(Endpoints.FETCH_DAILY_SAVING_EDUCATION)
            parameter("contentType", BaseConstants.StaticContentType.DAILY_SAVINGS_STEPS.name)
        }
    }

    suspend fun fetchAbandonBottomSheetData(
        contentType : String
    ) = getResult<ApiResponseWrapper<AbandonScreenBottomSheetResponse>> {
        client.get {
            url(Endpoints.FETCH_ABANDON_DAILY_INVESTMENT_DATA)
            parameter(
                "contentType",
                contentType
            )
        }
    }

    suspend fun fetchAmountSelectionScreenData(
    ) = getResult<ApiResponseWrapper<AmountSelectionResp?>> {
        client.get {
            url(Endpoints.FETCH_AMOUNT_SELECTION_DATA)
            parameter("contentType", BaseConstants.StaticContentType.DAILY_SAVINGS_BENEFITS.name)
        }
    }

    suspend fun fetchDailySavingsFaqData(
    ) = getResult<ApiResponseWrapper<ExpandableFaqResponse>> {
        client.get {
            url(Endpoints.FETCH_DAILY_INVESTMENT_FAQ)
            parameter("contentType", BaseConstants.StaticContentType.DAILY_SAVINGS_FAQ.name)
        }
    }


    suspend fun fetchAutoGoldInvestedSavings() =
        getResult<ApiResponseWrapper<PostPaymentReward?>> {
            client.get {
                url(Endpoints.FETCH_AUTOPAY_LANDING_DATA)
            }
        }

    suspend fun fetchDailySavingsMandate() =
        getResult<ApiResponseWrapper<DailyInvestmentMandateBottomSheetData?>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_MANDATE_DATA)
            }
        }

    suspend fun fetchDailySavingsIntroBottomSheetData(
    ) = getResult<ApiResponseWrapper<DailyInvestmentIntroData>> {
        client.get {
            url(Endpoints.FETCH_DAILY_INVESTMENT_INTRO_BOTTOM_SHEET_DATA)
            parameter(
                "contentType",
                BaseConstants.StaticContentType.DAILY_SAVINGS_ABANDONED_STEPS.name
            )
        }
    }

    suspend fun fetchSavingDetails(savingsType: String) = getResult<ApiResponseWrapper<DailyInvestmentIntroData>> {
        client.get {
            url(Endpoints.FETCH_DAILY_INVESTMENT_INTRO_BOTTOM_SHEET_DATA)
            parameter(
                "contentType",
                BaseConstants.StaticContentType.DAILY_SAVINGS_ABANDONED_STEPS.name
            )
        }
    }

    suspend fun fetchDailyInvestmentStoriesData() =
        getResult<ApiResponseWrapper<DSOnboardingStoryData?>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_STORIES)
                parameter(
                    "contentType",
                    BaseConstants.StaticContentType.ONBOARDING_STORIES.name
                )
            }
        }

    suspend fun fetchDailyInvestmentOnboardingFragmentData(version : String?) =
        getResult<ApiResponseWrapper<DailyInvestmentOnboardingFragmentData?>> {
            client.get {
                url(Endpoints.FETCH_DAILY_INVESTMENT_ONBOARDING_FRAGMENT_DATA)
                parameter(
                    "version",
                    version
                )
            }
        }

    suspend fun fetchUpdateDailyInvestmentStaticData() =
        getResult<ApiResponseWrapper<UpdateDailyInvestmentStaticData?>> {
            client.get {
                url(Endpoints.FETCH_UPDATE_DAILY_INVESTMENT_STATIC_DATA)
            }
        }
}