package com.jar.app.feature_sell_gold.shared.ui.amount

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.GoldPriceFlow
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_sell_gold.shared.domain.models.DrawerDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.GoldPriceState
import com.jar.app.feature_sell_gold.shared.domain.models.KycDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchDrawerDetailsUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchKycDetailsForSellGoldUseCase
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.BackButton
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class AmountEntryViewModel constructor(
    private val goldPriceFlow: GoldPriceFlow,
    private val fetchDrawerDetailsUseCase: FetchDrawerDetailsUseCase,
    private val fetchKycDetailsForSellGoldUseCase: FetchKycDetailsForSellGoldUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val analytics: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main.immediate)

    private var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    private val _volumeFromAmountFlow = MutableStateFlow<Float?>(null)
    val volumeFromAmountFlow: CStateFlow<Float?>
        get() = _volumeFromAmountFlow.toCommonStateFlow()

    private val _currentGoldSellPriceFlow = MutableStateFlow<GoldPriceState?>(null)
    val currentGoldSellPriceFlow: CStateFlow<GoldPriceState?>
        get() = _currentGoldSellPriceFlow.toCommonStateFlow()

    private var _drawerDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DrawerDetailsResponse?>>>(
            RestClientResult.none()
        )
    val drawerDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DrawerDetailsResponse?>>>
        get() = _drawerDetailsFlow.toCommonStateFlow()

    private var _kycDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycDetailsResponse?>>>(
            RestClientResult.none()
        )
    val kycDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KycDetailsResponse?>>>
        get() = _kycDetailsFlow.toCommonStateFlow()

    fun fetchData() {
        collectGoldSellPriceFlow()
        fetchDrawerDetails()
        fetchKycDetails()
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

    fun fetchKycDetails() {
        viewModelScope.launch {
            fetchKycDetailsForSellGoldUseCase().collect {
                _kycDetailsFlow.value = it
            }
        }
    }

    fun fetchDrawerDetails() {
        viewModelScope.launch {
            fetchDrawerDetailsUseCase().collect {
                _drawerDetailsFlow.value = it
            }
        }
    }

    fun calculateVolumeFromAmount(amount: Float) {
        viewModelScope.launch {
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentGoldPriceResponse)
                .collect {
                    _volumeFromAmountFlow.value = it.data
                }
        }
    }

    fun postSellGoldScreenShownEvent(
        drawerDetailsResponse: DrawerDetailsResponse,
        kycDetails: KycDetailsResponse?
    ) {
        if (drawerDetailsResponse.drawer.drawerItems.size >= 2) {
            analytics.postEvent(
                event = SellGoldEvent.Shown_Screen_SellGold_Details,
                values = mapForShownScreenSellGoldDetailsEvent(drawerDetailsResponse, kycDetails)
            )
        }
    }

    fun postSellGoldBackButtonClickedEvent() {
        analytics.postEvent(
            event = SellGoldEvent.SellGold_Button_Clicked,
            values = mapOf(
                SellGoldEvent.Button to BackButton,
                SellGoldEvent.IdentityVerified to (_kycDetailsFlow.value.data?.data?.docType != null)
            )
        )
    }

    fun postSellGoldFaqButtonClickedEvent() {
        analytics.postEvent(SellGoldEvent.SellGold_Button_Clicked, SellGoldEvent.Faqs)
    }

    fun postVerifyPanButtonClicked() {
        analytics.postEvent(
            event = SellGoldEvent.Withdrawal_IDVerificationBS_Clicked,
            key = SellGoldEvent.ButtonClicked,
            value = SellGoldEvent.VerifyPan
        )
    }

    fun postContactUsButtonClicked() {
        analytics.postEvent(
            event = SellGoldEvent.Withdrawal_IDVerificationBS_Clicked,
            key = SellGoldEvent.ButtonClicked,
            value = SellGoldEvent.ContactUs
        )
    }

    fun postVerifyIdButtonClickedEvent() {
        analytics.postEvent(SellGoldEvent.Withdrawal_IDVerificationBS_Clicked, SellGoldEvent.VerifyId)
    }

    fun postWithdrawDetailsToggledEvent() {
        analytics.postEvent(
            SellGoldEvent.SellGold_Button_Clicked,
            SellGoldEvent.InformationDropdown
        )
    }

    fun postSavingsValueScreenEvent(
        hasViewedDrawerDetailsOnce: Boolean,
        hasEnteredValidAmountOnce: Boolean,
        hasGoldPriceUpdatedAtLeastOnce: Boolean,
        currentValuePropCarouselPage: Int,
        drawerDetailsResponse: DrawerDetailsResponse,
        kycDetails: KycDetailsResponse?
    ) {
        analytics.postEvent(
            event = SellGoldEvent.Clicked_Button_SavingsValueScreen,
            values = mapForClickedButtonSavingsValueScreenEvent(
                hasViewedDrawerDetailsOnce,
                hasEnteredValidAmountOnce,
                hasGoldPriceUpdatedAtLeastOnce,
                currentValuePropCarouselPage,
                drawerDetailsResponse,
                kycDetails
            )
        )
    }

    fun postSellGoldMoneyEnteredEvent(
        amount: Float,
        hasShownVerificationSheetAtLeastOnce: Boolean
    ) {
        analytics.postEvent(
            event = SellGoldEvent.SellGoldScreen_MoneyEntered,
            values = mapOf(
                SellGoldEvent.AMOUNT to amount,
                SellGoldEvent.GoldWeight to _volumeFromAmountFlow.value.orZero(),
                SellGoldEvent.IdentityVerified to (_kycDetailsFlow.value.data?.data?.docType != null),
                SellGoldEvent.VerifyIdCardShown to hasShownVerificationSheetAtLeastOnce
            )
        )
    }

    fun postWithdrawalVerificationSheetOpenedEvent() {
        analytics.postEvent(SellGoldEvent.Withdrawal_IDVerificationBS_Shown)
    }
}

private fun mapForShownScreenSellGoldDetailsEvent(
    drawerDetailsResponse: DrawerDetailsResponse,
    kycDetails: KycDetailsResponse?
) = mapOf(
    SellGoldEvent.FlowType to SellGoldEvent.NewWithdrawal,
    SellGoldEvent.IdentityVerified to (kycDetails?.docType != null),
    SellGoldEvent.UnavailableAmountToSell to with(drawerDetailsResponse.drawer) {
        drawerItems[0].amount.toFloat() - drawerDetailsResponse.drawer.drawerItems[1].amount.toFloat()
    }
) + drawerDetailsResponse.drawer.drawerItems.associate { it.keyText to it.amount }

private fun mapForClickedButtonSavingsValueScreenEvent(
    hasViewedDrawerDetailsOnce: Boolean,
    hasEnteredValidAmountOnce: Boolean,
    hasGoldPriceUpdatedAtLeastOnce: Boolean,
    currentValuePropCarouselPage: Int,
    drawerDetailsResponse: DrawerDetailsResponse?,
    kycDetails: KycDetailsResponse?
) = mapOf(
    SellGoldEvent.ButtonClicked to SellGoldEvent.Proceed,
    SellGoldEvent.ViewedInformationDropdown to hasViewedDrawerDetailsOnce,
    SellGoldEvent.CardShown to hasEnteredValidAmountOnce,
    SellGoldEvent.Title to drawerDetailsResponse?.withdrawalCards?.get(currentValuePropCarouselPage)?.description.orEmpty(),
    SellGoldEvent.GoldPriceChangeTriggered to hasGoldPriceUpdatedAtLeastOnce,
    SellGoldEvent.IdentityVerified to kycDetails?.docType.isNullOrBlank().not()
)

