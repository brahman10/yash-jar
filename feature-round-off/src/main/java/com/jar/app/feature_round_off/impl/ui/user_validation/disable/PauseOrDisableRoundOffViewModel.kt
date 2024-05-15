package com.jar.app.feature_round_off.impl.ui.user_validation.disable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PauseOrDisableRoundOffViewModel @Inject constructor(private val disableUserSavingsUseCase: DisableUserSavingsUseCase) :
    ViewModel() {

    private val _disableRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val disableRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _disableRoundOffLiveData

    fun disableRoundOff(){
        viewModelScope.launch {
            disableUserSavingsUseCase.disableSavings(SavingsType.ROUND_OFFS).collect{
                _disableRoundOffLiveData.postValue(it)
            }
        }
    }
}