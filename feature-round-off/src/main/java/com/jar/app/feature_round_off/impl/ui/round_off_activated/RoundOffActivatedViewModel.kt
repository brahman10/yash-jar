package com.jar.app.feature_round_off.impl.ui.round_off_activated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
internal class RoundOffActivatedViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase
) :
    ViewModel() {

    private val _updateUserSettingsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateUserSettingsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateUserSettingsLiveData

    fun updateUserRoundOffs() {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(investRoundOffs = true))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _updateUserSettingsLiveData.postValue(it)
                }
        }
    }

    fun enableManualRoundOff() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.ROUND_OFFS,
                enableAutoSave = false
            ).collect {
                EventBus.getDefault()
                    .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
            }
        }
    }
}