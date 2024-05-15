package com.jar.app.feature_promo_code.shared.ui

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.app.feature_promo_code.shared.domain.use_cases.FetchPromoCodeTransactionStatusUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PromoCodeStatusViewModel constructor(
    private val fetchPromoCodeTransactionStatusUseCase: FetchPromoCodeTransactionStatusUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _promoCodeTransactionResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PromoCodeTransactionResponse?>>>()
    val promoCodeTransactionResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<PromoCodeTransactionResponse?>>>
        get() = _promoCodeTransactionResponseFlow.toCommonFlow()

    fun fetchPromoCodeTransactionStatus(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchPromoCodeTransactionStatusUseCase.fetchPromoCodeTransactionStatus(orderId)
                .collectLatest {
                    _promoCodeTransactionResponseFlow.emit(it)
                }
        }
    }

    fun postAnalyticsEvent(eventName: String, value: Map<String, Any>? = null) {
        value?.let {
            analyticsApi.postEvent(eventName, value)
        } ?: run {
            analyticsApi.postEvent(eventName)
        }
    }

}