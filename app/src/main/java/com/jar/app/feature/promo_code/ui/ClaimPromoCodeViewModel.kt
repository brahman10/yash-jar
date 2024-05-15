package com.jar.app.feature.promo_code.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.ApplyPromoResponse
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature.home.domain.usecase.ApplyPromoCodeUseCase
import com.jar.app.feature.promo_code.domain.data.network.PromoCodePagingSource
import com.jar.app.feature.promo_code.domain.use_case.FetchPromoCodeUseCase
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.base.data.livedata.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ClaimPromoCodeViewModel @Inject constructor(
    private val applyPromoCodeUseCase: ApplyPromoCodeUseCase,
    private val fetchPromoCodeUseCase: FetchPromoCodeUseCase,
    private val deviceUtils: DeviceUtils
) : ViewModel() {
    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    private val _applyCouponCodeLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<ApplyPromoResponse>>>()
    val applyCouponCodeLiveData: LiveData<RestClientResult<ApiResponseWrapper<ApplyPromoResponse>>>
        get() = _applyCouponCodeLiveData

    fun applyPromoCode(promoCode: String, id: String? = null, type: String? = null) {
        viewModelScope.launch {
            val deviceDetail = DeviceDetails(
                advertisingId = deviceUtils.getAdvertisingId(),
                deviceId = deviceUtils.getDeviceId(),
                os = deviceUtils.getOsName()
            )
            applyPromoCodeUseCase.applyPromoCode(
                deviceDetail, promoCode, id, type
            ).collect {
                _applyCouponCodeLiveData.postValue(it)
            }
        }
    }

    fun fetchPromoCodes() = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            initialLoadSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PromoCodePagingSource(fetchPromoCodeUseCase)
        }
    ).flow.cachedIn(viewModelScope)
}