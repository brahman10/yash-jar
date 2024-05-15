package com.jar.app.feature_round_off.impl.ui.invest_money

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class InvestMoreMoneyViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) :
    ViewModel() {

    private val _updateUserSettingsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateUserSettingsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateUserSettingsLiveData

    fun updateUserInvestNoSpends() {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(autoInvestNoSpends = true))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _updateUserSettingsLiveData.postValue(it)
                }
        }
    }
}