package com.jar.feature_quests.impl.ui.coupon_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class QuestCouponDetailsViewModel @Inject constructor(
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val analyticsApi: AnalyticsApi
): ViewModel() {

    private val _couponDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>()
    val couponDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>
        get() = _couponDetailsFlow.toCommonFlow()

    fun fetchCouponDetails(brandCouponId: String) {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponDetails(brandCouponId)
                .collectLatest { couponResponse ->
                    _couponDetailsFlow.emit(couponResponse)
                }
        }
    }

    fun fireShownEvent() {
        analyticsApi.postEvent(QuestEventKey.Events.Shown_CouponDetailsPage)
    }

    fun fireClickedEvent(buttonType: String) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Clicked_CouponDetailsPage,
            mapOf(
                QuestEventKey.Properties.button_type to buttonType
            )
        )
    }
}