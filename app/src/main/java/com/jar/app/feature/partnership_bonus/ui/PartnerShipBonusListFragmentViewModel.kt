package com.jar.app.feature.partnership_bonus.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannerList
import com.jar.app.feature_homepage.shared.domain.use_case.ClaimBonusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchPartnerBannerUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartnerShipBonusListFragmentViewModel @Inject constructor(
    private val claimBonusUseCase: ClaimBonusUseCase,
    private val fetchPartnerBannerUseCase: FetchPartnerBannerUseCase,
): ViewModel() {

    private val _partnerBannerLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<BannerList>>>()
    val partnerBannerLiveData: LiveData<RestClientResult<ApiResponseWrapper<BannerList>>>
        get() = _partnerBannerLiveData

    private val _claimedBonusLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val claimedBonusLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _claimedBonusLiveData

    fun fetchPartnerBanners() {
        viewModelScope.launch {
            fetchPartnerBannerUseCase.fetchPartnerBanners(includeView = false).collect {
                _partnerBannerLiveData.postValue(it)
            }
        }
    }

    fun claimBonus(orderId: String) {
        viewModelScope.launch {
            claimBonusUseCase.claimBonus(orderId).collectLatest {
                _claimedBonusLiveData.postValue(it)
            }
        }
    }
}