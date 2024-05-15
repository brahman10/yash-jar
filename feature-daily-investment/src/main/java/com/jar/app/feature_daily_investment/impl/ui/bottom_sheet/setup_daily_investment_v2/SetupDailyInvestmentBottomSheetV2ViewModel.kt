package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.setup_daily_investment_v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.addPercentage
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentBottomSheetV2Data
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentBottomSheetV2UseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SetupDailyInvestmentBottomSheetV2ViewModel @Inject constructor(
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val fetchDailyInvestmentBottomSheetV2UseCase: FetchDailyInvestmentBottomSheetV2UseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val appScope: CoroutineScope,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val _dsSuggestedAmountDetails =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>(RestClientResult.none())
    val dsSuggestedAmountDetails: CStateFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSuggestedAmountDetails.toCommonStateFlow()

    private val _screenStaticDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentBottomSheetV2Data?>>>(
            RestClientResult.none()
        )
    val screenStaticDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentBottomSheetV2Data?>>>
        get() = _screenStaticDataFlow.toCommonStateFlow()

    private val _isAutoPayResetRequiredFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredFlow: CFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredFlow.toCommonFlow()

    private val _roundOffDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(RestClientResult.none())
    val roundOffDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsFlow.toCommonStateFlow()


    private val _updateDailySavingStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>(RestClientResult.none())
    val updateDailySavingStatusFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusFlow.toCommonStateFlow()

    private val _rVFlowData = MutableStateFlow<List<SuggestedRecurringAmount>>(emptyList())
    val rVFlowData: CStateFlow<List<SuggestedRecurringAmount>>
        get() = _rVFlowData.toCommonStateFlow()

    var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    private val _couponCodesFlow =
        MutableStateFlow<RestClientResult<List<CouponCode>>>(RestClientResult.none())
    val couponCodesFlow: CFlow<RestClientResult<List<CouponCode>>>
        get() = _couponCodesFlow.toCommonFlow()

    private val _recommendedAmountFlow = MutableStateFlow<Int>(0)
    val recommendedAmountFlow: CStateFlow<Int> = _recommendedAmountFlow.toCommonStateFlow()

    var couponCodeList: List<CouponCode>? = null

    private var updateDailySavingJob: Job? = null

    var amount = 0f
    var maxDSAmount = 15000f
    var minDSAmount = 10f
    var couponCodeId: String? = null
    var recommendedAmount = 0f
    var popularAmount = 0f
    var couponCodeMinimumAmount = 0

    suspend fun setRecommendedAmount(value: Int) {
        _recommendedAmountFlow.emit(value)
    }

    fun fetchScreenStaticData() {
        viewModelScope.launch {
            fetchDailyInvestmentBottomSheetV2UseCase.fetchDailyInvestmentBottomSheetV2Data()
                .collect {
                    _screenStaticDataFlow.emit(it)
                }
        }
    }

    fun fetchSuggestedAmount() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS
            ).collect {
                _dsSuggestedAmountDetails.emit(it)
            }
        }
    }

    fun enableOrUpdateDailySaving(amount: Float) {
        updateDailySavingJob?.cancel()
        updateDailySavingJob = appScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusFlow.emit(it)
                }
        }
    }

    fun getFinalVolume(amount: Float): Float {
        return fetchCurrentGoldPriceResponse?.let {
            val currentPriceWithTax = it.price.addPercentage(it.applicableTax!!)
                .roundUp(2)
            return (amount * 365 / currentPriceWithTax).roundDown(4)
        } ?: kotlin.run {
            5f
        }
    }

    suspend fun createRvListData(savingSetupInfo: SavingSetupInfo) {
        val list = mutableListOf<SuggestedRecurringAmount>()
        savingSetupInfo.options.forEach {
            SuggestedRecurringAmount(it.amount, it.recommended)
            list.add(SuggestedRecurringAmount(it.amount, it.recommended))
        }
        _rVFlowData.emit(list)
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredFlow.emit(it)
                analyticsApi.postEvent(
                    DailySavingsEventKey.DailySavings_AmountBSClicked,
                    mapOf(
                        DailySavingsEventKey.Action to DailySavingsEventKey.ProceedClicked,
                        DailySavingsEventKey.FromScreen to DailySavingsEventKey.SinglePageHomeFeedFlow,
                        DailySavingsEventKey.amount to amount,
                        DailySavingsEventKey.CouponStatus to if (couponCodeList.isNullOrEmpty()) "not_available" else "available",
                        DailySavingsEventKey.CouponApplied to if (amount < couponCodeMinimumAmount) "false" else "true",
                        DailySavingsEventKey.IsPopularAmountSelected to if (amount == recommendedAmount) "true" else "false",
                        DailySavingsEventKey.IsRecommendedAmountSelected to if (amount == popularAmount) "true" else "false",
                    )
                )
            }
        }
    }

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect{
                _roundOffDetailsFlow.emit(it)
            }
        }
    }

    fun fetchCouponCodes(context: String) {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponCodes(context = context).collect(
                onLoading = {
                    _couponCodesFlow.emit(RestClientResult.loading())
                },
                onSuccess = {
                    couponCodeList = it?.couponCodes
                    _couponCodesFlow.emit(RestClientResult.success(couponCodeList.orEmpty()))
                    analyticsApi.postEvent(
                        DailySavingsEventKey.DailySavings_AmountBSLaunched,
                        mapOf(
                            DailySavingsEventKey.FromScreen to DailySavingsEventKey.SinglePageHomeFeedFlow,
                            EventKey.Coupon_Status to if (couponCodeList.isNullOrEmpty()) "not_available" else "available"
                        )
                    )
                },
                onError = { errorMessage, _ ->
                    _couponCodesFlow.emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }
}