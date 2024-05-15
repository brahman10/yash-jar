package com.jar.app.feature_round_off.impl.ui.user_validation.auto_save.resume

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundOffAutoSaveResumeViewModel @Inject constructor(
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase
) : ViewModel() {

    private val _isAutoPayResetRequiredLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.ROUND_OFFS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
            }
        }
    }
}