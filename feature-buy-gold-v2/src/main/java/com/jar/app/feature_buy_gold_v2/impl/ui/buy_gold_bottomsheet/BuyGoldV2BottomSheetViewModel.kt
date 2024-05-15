package com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold_bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.SuggestedAmountData
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldBottomSheetV2UseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchSuggestedAmountUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BuyGoldV2BottomSheetViewModel @Inject constructor(
    private val fetchSuggestedAmountUseCase: FetchSuggestedAmountUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchBuyGoldBottomSheetV2UseCase: FetchBuyGoldBottomSheetV2UseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val _suggestedAmountFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>()
    val suggestedAmountFlow: CFlow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>
        get() = _suggestedAmountFlow.toCommonFlow()

    private val _couponCodesFlow =
        MutableStateFlow<RestClientResult<List<CouponCode>>>(RestClientResult.none())
    val couponCodesFlow: CStateFlow<RestClientResult<List<CouponCode>>>
        get() = _couponCodesFlow.toCommonStateFlow()

    private val _currentGoldBuyPriceFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val currentGoldBuyPriceFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceFlow.toCommonFlow()

    private val _screenStaticDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>>>(
            RestClientResult.none()
        )
    val screenStaticDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>>>
        get() = _screenStaticDataFlow.toCommonStateFlow()

    private val _buyGoldFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldFlow.toCommonFlow()


    var couponCodeList: List<CouponCode>? = null

    var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null

    private var fetchBuyPriceJob: Job? = null

    var buyAmount = 0f
    var couponCodeMinimumAmount = 0
    var popularAmount = 101f
    var recommmendedAmount = 0f
    var buyGoldRequestType = BuyGoldRequestType.AMOUNT

    fun fetchBuyGoldBottomSheetV2Data() {
        viewModelScope.launch {
            fetchBuyGoldBottomSheetV2UseCase.fetchBuyGoldBottomSheetV2Data()
                .collect {
                    _screenStaticDataFlow.emit(it)
                }
        }
    }

    fun fetchSuggestedAmount(selectedCouponCode: String?, flowContext: String?) {
        viewModelScope.launch {
            fetchSuggestedAmountUseCase.fetchSuggestedAmount(flowContext, selectedCouponCode)
                .collect {
                    it.data?.data?.suggestedAmount?.options?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        } ?: kotlin.run {
                        it.data?.data?.suggestedAmount?.options?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                            ?.let { bestOption ->
                                bestOption.isBestTag = true
                            }
                    }
                    it.data?.data?.suggestedAmount?.volumeOptions?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        } ?: kotlin.run {
                        it.data?.data?.suggestedAmount?.volumeOptions?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                            ?.let { bestOption ->
                                bestOption.isBestTag = true
                            }
                    }
                    _suggestedAmountFlow.emit(it)
                }
        }
    }

    fun fetchCurrentGoldBuyPrice() {
        fetchBuyPriceJob?.cancel()
        fetchBuyPriceJob = viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collectUnwrapped(
                    onLoading = {
                        _currentGoldBuyPriceFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        fetchCurrentBuyPriceResponse = it.data
                        _currentGoldBuyPriceFlow.emit(RestClientResult.success(it))
                    },
                    onError = { errorMessage, errorCode ->
                        _currentGoldBuyPriceFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }

    fun buyGoldByAmount(buyGoldByAmountRequest: BuyGoldByAmountRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByAmount(buyGoldByAmountRequest).collect {
                _buyGoldFlow.emit(it)
                analyticsApi.postEvent(
                    BuyGoldV2EventKey.BuyGold_AmountBSClicked,
                    mapOf(
                        BuyGoldV2EventKey.Amount to buyAmount,
                        BuyGoldV2EventKey.CouponStatus to if (couponCodeList.isNullOrEmpty()) "not_available" else "available",
                        BuyGoldV2EventKey.CouponApplied to if (buyAmount < couponCodeMinimumAmount) "false" else "true",
                        BuyGoldV2EventKey.FromScreen to "SinglePageHomeScreenFlow",
                        BuyGoldV2EventKey.IsPopularAmountSelected to if (buyAmount == popularAmount) "true" else "false",
                        )
                )
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
                        BuyGoldV2EventKey.BuyGold_AmountBSLaunched,
                        mapOf(
                            BuyGoldV2EventKey.From to BuyGoldV2EventKey.SinglePageHomeScreenFlow,
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