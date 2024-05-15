package com.jar.app.feature_sell_gold.shared.ui.vpa

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.GoldPriceFlow
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_sell_gold.shared.domain.models.GoldPriceState
import com.jar.app.feature_sell_gold.shared.domain.models.UpiVerificationStatus
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.ButtonClicked
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Screen
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.app.feature_settings.domain.model.VerifyUpiResponse
import com.jar.app.feature_settings.domain.model.VpaChips
import com.jar.app.feature_settings.domain.use_case.AddNewUpiIdUseCase
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase
import com.jar.app.feature_user_api.domain.model.SavedVpaResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VpaSelectionViewModel(
    private val goldPriceFlow: GoldPriceFlow,
    private val fetchUserSavedVpaUseCase: FetchUserVpaUseCase,
    private val fetchVpaChipUseCase: FetchVpaChipUseCase,
    private val verifyUpiUseCase: VerifyUpiUseCase,
    private val addNewUpiIdUseCase: AddNewUpiIdUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val analytics: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main.immediate)

    private var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    private val _currentGoldSellPriceFlow = MutableStateFlow<GoldPriceState?>(null)
    val currentGoldSellPriceFlow: CStateFlow<GoldPriceState?>
        get() = _currentGoldSellPriceFlow.toCommonStateFlow()

    private val _volumeFromAmountFlow = MutableStateFlow<Float?>(null)
    val volumeFromAmountFlow: CStateFlow<Float?>
        get() = _volumeFromAmountFlow.toCommonStateFlow()

    private val _userSavedVpasFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>(
            RestClientResult.none()
        )
    val userSavedVpasFlow: CStateFlow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>
        get() = _userSavedVpasFlow.toCommonStateFlow()

    private val _vpaChipsFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>(
        RestClientResult.none()
    )
    val vpaChipsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<VpaChips>>>
        get() = _vpaChipsFlow.toCommonStateFlow()

    private val _upiVerificationStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UpiVerificationStatus>>>(
            RestClientResult.none()
        )
    val upiVerificationStatusFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UpiVerificationStatus>>>
        get() = _upiVerificationStatusFlow.toCommonStateFlow()

    private val _withdrawRequestStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>(
            RestClientResult.none()
        )
    val withdrawRequestStatusFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>
        get() = _withdrawRequestStatusFlow.toCommonStateFlow()

    init {
        collectGoldSellPriceFlow()
        fetchUserSavedVpas()
        fetchSuggestedVpaChips()
    }

    private fun collectGoldSellPriceFlow() {
        combine(goldPriceFlow.sellPrice, goldPriceFlow.priceTimer) { sellPrice, priceTimer ->
            fetchCurrentGoldPriceResponse = sellPrice.data
            _currentGoldSellPriceFlow.value = GoldPriceState(
                rateId = sellPrice.data?.rateId.orEmpty(),
                rateValidity = sellPrice.data?.rateValidity.orEmpty(),
                goldPrice = sellPrice.data?.price.orZero(),
                validityInSeconds = sellPrice.data?.validityInSeconds ?: 0L,
                millisLeft = priceTimer,
                isPriceDrop = sellPrice.data?.isPriceDrop.orFalse()
            )
        }.launchIn(viewModelScope)
    }

    fun calculateVolumeFromAmount(amount: Float) {
        viewModelScope.launch {
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentGoldPriceResponse)
                .collect { _volumeFromAmountFlow.value = it.data }
        }
    }

    private fun fetchUserSavedVpas() {
        viewModelScope.launch {
            fetchUserSavedVpaUseCase.fetchUserSavedVPAs().collect {
                _userSavedVpasFlow.value = it
            }
        }
    }

    private fun fetchSuggestedVpaChips() {
        viewModelScope.launch {
            fetchVpaChipUseCase.fetchVpaChips().collect {
                _vpaChipsFlow.value = it
            }
        }
    }

    fun verifyUpiAddress(upiAddress: String) {
        viewModelScope.launch {
            verifyUpiUseCase.verifyUpiAddress(upiAddress).collect(
                onLoading = {
                    _upiVerificationStatusFlow.update {
                        RestClientResult(
                            status = RestClientResult.Status.LOADING,
                            data = ApiResponseWrapper(
                                data = UpiVerificationStatus(isLoading = true),
                                success = true
                            )
                        )
                    }
                },
                onSuccess = { verifyUpiResponse ->
                    addNewUpiId(upiAddress, verifyUpiResponse)
                },
                onError = { _, _ ->
                    _upiVerificationStatusFlow.update {
                        RestClientResult(
                            status = RestClientResult.Status.SUCCESS,
                            data = ApiResponseWrapper(
                                data = UpiVerificationStatus(
                                    isLoading = false,
                                    isError = true
                                ),
                                success = false
                            )
                        )
                    }
                }
            )
        }
    }

    fun addNewUpiId(upiAddress: String, verifyUpiResponse: VerifyUpiResponse) {
        viewModelScope.launch {
            addNewUpiIdUseCase.addNewUpiId(upiAddress)
                .collect(
                    onSuccess = {
                        it?.let { newlySavedVpa ->
                            val currentSavedVpas =
                                _userSavedVpasFlow.value.data?.data?.payoutSavedVpas ?: emptyList()

                            _upiVerificationStatusFlow.update {
                                RestClientResult(
                                    status = RestClientResult.Status.SUCCESS,
                                    data = ApiResponseWrapper(
                                        data = UpiVerificationStatus(
                                            isLoading = false,
                                            verifyUpiResponse = verifyUpiResponse
                                        ),
                                        success = true
                                    )
                                )
                            }

                            _userSavedVpasFlow.update {
                                RestClientResult(
                                    status = RestClientResult.Status.SUCCESS,
                                    data = ApiResponseWrapper(
                                        data = it.data?.data?.copy(listOf(newlySavedVpa) + currentSavedVpas)!!,
                                        success = true
                                    )
                                )
                            }
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        _upiVerificationStatusFlow.update {
                            RestClientResult(
                                status = RestClientResult.Status.SUCCESS,
                                data = ApiResponseWrapper(
                                    data = UpiVerificationStatus(
                                        isLoading = false,
                                        isError = true,
                                        errorMessage = errorMessage
                                    ),
                                    success = false,
                                    errorMessage = errorMessage,
                                    errorCode = errorCode?.toInt(),
                                    toastMessage = errorMessage
                                )
                            )
                        }
                    }
                )
        }
    }

    fun postVpaListScreenShownEvent() {
        analytics.postEvent(
            event = SellGoldEvent.Shown_Screen_SellGold_VPA_List,
            values = mapOf(
                Screen to SellGoldEvent.SelectAccount,
                SellGoldEvent.IsPrimaryUpiIdShown to _userSavedVpasFlow.value.data?.data?.payoutSavedVpas
                    ?.any { it.isPrimaryUpi == true }
                    .orFalse(),
                SellGoldEvent.UpiListSize to _userSavedVpasFlow.value.data?.data?.payoutSavedVpas?.size.orZero()
            )
        )
    }

    fun postWithdrawConfirmClickedEvent() {
        analytics.postEvent(
            event = SellGoldEvent.Clicked_Confirm_WithdrawSummaryScreen,
            values = mapOf(
                SellGoldEvent.GoldPriceChangeTriggered to false,
                ButtonClicked to SellGoldEvent.Confirm,
                SellGoldEvent.GoldValueDecreased to (_currentGoldSellPriceFlow.value?.isPriceDrop == true)
            )
        )
    }
}