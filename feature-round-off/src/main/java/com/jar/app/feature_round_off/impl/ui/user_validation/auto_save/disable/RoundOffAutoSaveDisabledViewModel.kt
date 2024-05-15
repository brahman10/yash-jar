package com.jar.app.feature_round_off.impl.ui.user_validation.auto_save.disable

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundOffAutoSaveDisabledViewModel @Inject constructor(
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase
) : ViewModel() {

    private val _disableAutoSaveRoundOffLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val disableAutoSaveRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _disableAutoSaveRoundOffLiveData

    fun disableUserRoundOffAutoSave() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.ROUND_OFFS,
                enableAutoSave = false
            ).collect{
                _disableAutoSaveRoundOffLiveData.postValue(it)
            }
        }
    }
}