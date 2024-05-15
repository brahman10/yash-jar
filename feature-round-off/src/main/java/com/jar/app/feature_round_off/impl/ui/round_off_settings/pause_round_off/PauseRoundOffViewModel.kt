package com.jar.app.feature_round_off.impl.ui.round_off_settings.pause_round_off

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PauseRoundOffViewModel @Inject constructor(
    private val updateSavingPauseDurationUseCase: com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
): ViewModel() {

    private val _pauseRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val pauseRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _pauseRoundOffLiveData

    fun updateAutoInvestPauseDuration(pauseRoundOffOption: PauseRoundOffOption) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause = true,
                pauseDuration = pauseRoundOffOption.name,
                savingType = SavingsType.ROUND_OFFS
            ).collect {
                _pauseRoundOffLiveData.postValue(it)
            }
        }
    }
}