package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.brand_coupons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jar.app.feature_buy_gold_v2.impl.data.paging_source.BrandCouponsPagingSource
import com.jar.app.feature_buy_gold_v2.shared.ui.AuspiciousDatesViewModel.Companion.NETWORK_PAGE_SIZE
import com.jar.app.feature_buy_gold_v2.shared.data.network.BuyGoldV2DataSource
import com.jar.app.feature_coupon_api.data.network.CouponDataSource
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandCouponData
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandsCouponInfo
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrandCouponViewModel @Inject constructor(
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val couponDataSource: CouponDataSource
) :
    ViewModel() {

    private val _brandCouponsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<BrandCouponData>>>()
    val brandCouponsLiveData: LiveData<RestClientResult<ApiResponseWrapper<BrandCouponData>>>
        get() = _brandCouponsLiveData

    private val _brandCouponsPaginatedLiveData =
        MutableLiveData<PagingData<BrandsCouponInfo>>()
    val brandCouponsPaginatedLiveData: LiveData<PagingData<BrandsCouponInfo>>
        get() = _brandCouponsPaginatedLiveData

    fun fetchBrandCouponsWithPaging() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                pagingSourceFactory = { BrandCouponsPagingSource(couponDataSource) }
            ).flow.cachedIn(viewModelScope).collectLatest {
                _brandCouponsPaginatedLiveData.postValue(it)
            }
        }
    }

    fun fetchBrandCouponInfo() {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchBrandCouponsWithoutPaging().collect{
                _brandCouponsLiveData.postValue(it)
            }
        }
    }
}