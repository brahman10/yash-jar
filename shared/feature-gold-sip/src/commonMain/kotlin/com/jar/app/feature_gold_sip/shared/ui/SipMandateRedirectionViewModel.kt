package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.core_base.util.addPercentage
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_gold_sip.shared.domain.model.SipGoldVolumeYearAmount
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SipMandateRedirectionViewModel constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldYearPriceFlow =
        MutableStateFlow<RestClientResult<SipGoldVolumeYearAmount>>(RestClientResult.none())
    val goldYearPriceFlow: CStateFlow<RestClientResult<SipGoldVolumeYearAmount>>
        get() = _goldYearPriceFlow.toCommonStateFlow()

    private val _buyPriceFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>(
            RestClientResult.none()
        )
    val buyPriceFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _buyPriceFlow.toCommonStateFlow()

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow:
            CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _updateGoldSipDetailsFlow.toCommonFlow()

    var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    fun fetchBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectLatest {
                _buyPriceFlow.emit(it)
            }
        }
    }
    fun getGoldYearAndPrice(
        sipSubscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType,
        years: Int,
        sipSelectedAmount: Float
    ) {
        viewModelScope.launch {
            var sipAmount = sipSelectedAmount * years
            fetchCurrentGoldPriceResponse?.let {
                sipAmount *= when (sipSubscriptionType) {
                    com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> 52
                    com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> 12
                }
                val currentPriceWithTax =
                    it.price.addPercentage(it.applicableTax!!).roundUp(2).orZero()
                val finalVolume = (sipAmount / currentPriceWithTax).roundDown(4)
                _goldYearPriceFlow.emit(
                    RestClientResult.success(
                        SipGoldVolumeYearAmount(finalVolume, years, sipAmount)
                    )
                )
            }
        }
    }


    fun updateGoldSip(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) {
        viewModelScope.launch {
            updateGoldSipDetailsUseCase.updateGoldSipDetails(updateSipDetails).collect {
                _updateGoldSipDetailsFlow.emit(it)
            }
        }
    }

}
