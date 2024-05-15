package com.jar.app.feature_daily_investment.impl.ui.ds_setup_state

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DSSuccessStateViewModel @Inject constructor(
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
) : ViewModel() {

    private val _dailySavingsDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dailySavingsDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingsDetailsLiveData

    fun fetchUserDailySavingsDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingsDetailsLiveData.postValue(it)
            }
        }
    }
}