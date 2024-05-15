package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.jar_coupons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponData
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JarCouponViewModel @Inject constructor(private val fetchCouponCodeUseCase: FetchCouponCodeUseCase) :
    ViewModel() {

    private val _couponCodesLiveData = MutableLiveData<RestClientResult<JarCouponData>>()
    val couponCodesLiveData: LiveData<RestClientResult<JarCouponData>>
        get() = _couponCodesLiveData


    private val _couponDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>()
    val couponDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<CouponDetailsResponse>>>
        get() = _couponDetailsLiveData

    fun fetchCouponDetails(brandCouponId: String) {

        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponDetails(brandCouponId)
                .collectLatest { couponResponse ->
                    _couponDetailsLiveData.postValue(couponResponse)
                }
        }

    }

    fun fetchJarCoupons(buyGoldFlowContext: String) {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchJarCoupons(context = buyGoldFlowContext)
                .collect(
                    onLoading = {
                        _couponCodesLiveData.postValue(RestClientResult.loading())
                    }, onSuccess = {

                        _couponCodesLiveData.postValue(RestClientResult.success(it))
                    }, onError = { errorMessage, _ ->
                        _couponCodesLiveData.postValue(RestClientResult.error(errorMessage))
                    }
                )
        }
    }
}