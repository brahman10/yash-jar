package com.jar.app.feature_round_off.impl.ui.round_off_settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.util.mapToDTO

@HiltViewModel
internal class RoundOffSettingsViewModel @Inject constructor(
    private val fetchIsSavingsPausedUseCase: com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase,
    private val updateSavingPauseDurationUseCase: com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private lateinit var roundOff: RoundOff

    private val _userSettingsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val userSettingsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _userSettingsLiveData

    private val _updateRoundOffValueLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateRoundOffValueLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateRoundOffValueLiveData

    private val _savingsPausedLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val savingsPausedLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _savingsPausedLiveData

    private val _updatePauseDurationLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val updatePauseDurationLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationLiveData

    private val _updateRoundOffStateLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateRoundOffStateLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateRoundOffStateLiveData

    private val _updateAutoInvestStateLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateAutoInvestStateLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateAutoInvestStateLiveData

    fun fetchUserSettings() {
        viewModelScope.launch {
            fetchUserSettingsUseCase.fetchUserSettings()
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collectLatest {
                    _userSettingsLiveData.postValue(it)
                }
        }
    }

    fun fetchIsRoundOffPaused() {
        viewModelScope.launch {
            fetchIsSavingsPausedUseCase.fetchIsSavingPaused(SavingsType.ROUND_OFFS)
                .collectLatest {
                    _savingsPausedLiveData.postValue(it)
                }
        }
    }

    fun updateRoundOffPauseDuration(pause: Boolean, pauseDuration: String?) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDuration,
                SavingsType.ROUND_OFFS
            )
                .collectLatest {
                    _updatePauseDurationLiveData.postValue(it)
                }
        }
    }

    fun updateRoundOffValue(roundOff: RoundOff) {
        this.roundOff = roundOff
    }

    fun updateUserRoundOffValue() {
        viewModelScope.launch {
            if (::roundOff.isInitialized) {
                updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(roundOffTo = roundOff.name))
                    .mapToDTO {
                        it?.toUserSettings()
                    }
                    .collect {
                        _updateRoundOffValueLiveData.postValue(it)
                    }
            }
        }
    }

    fun updateRoundOffState(isEnabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(investRoundOffs = isEnabled))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _updateRoundOffStateLiveData.postValue(it)
                }
        }
    }

    fun toggleAutoInvest(isEnabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(autoInvestNoSpends = isEnabled))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _updateAutoInvestStateLiveData.postValue(it)
                }
        }
    }

    enum class RoundOff {
        NEAREST_FIVE, NEAREST_TEN
    }
}