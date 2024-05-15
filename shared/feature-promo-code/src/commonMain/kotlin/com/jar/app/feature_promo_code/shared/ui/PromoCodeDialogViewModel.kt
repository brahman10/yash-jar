package com.jar.app.feature_promo_code.shared.ui


import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.app.feature_promo_code.shared.domain.use_cases.ApplyPromoCodeUseCase
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
 class PromoCodeDialogViewModel constructor(
     private val applyPromoCodeUseCase: ApplyPromoCodeUseCase,
     private val analyticsApi: AnalyticsApi,
     coroutineScope: CoroutineScope?
){
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _applyPromoCodeFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>>()
    val applyPromoCodeFlow: CFlow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>>
        get() = _applyPromoCodeFlow.toCommonFlow()

    fun applyPromoCode(promoCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
         applyPromoCodeUseCase.applyPromoCode(promoCode).collectLatest {
             _applyPromoCodeFlow.emit(it)
         }
        }
    }

     fun postAnalyticsEvent(eventName:String, value: Map<String, Any>? = null){
         value?.let {
             analyticsApi.postEvent(eventName,value)
         }?:run {
             analyticsApi.postEvent(eventName)
         }
     }

}