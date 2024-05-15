package com.jar.app.feature_round_off.impl.ui.save_method

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff
import com.jar.app.feature_round_off.shared.domain.model.RoundOffStepsResp
import com.jar.app.feature_round_off.shared.domain.model.RoundOffType
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchRoundOffStepsUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SelectRoundOffSaveMethodViewModel @Inject constructor(
    private val initialRoundOffUseCase: FetchInitialRoundOffUseCase,
    private val fetchRoundOffStepsUseCase: FetchRoundOffStepsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase
) : ViewModel() {

    private val _initialRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>()
    val initialRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>
        get() = _initialRoundOffLiveData

    private val _roundOffStepsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<RoundOffStepsResp>>>()
    val roundOffStepsLiveData: LiveData<RestClientResult<ApiResponseWrapper<RoundOffStepsResp>>>
        get() = _roundOffStepsLiveData

    fun fetchInitialRoundOffsData() {
        viewModelScope.launch {
            initialRoundOffUseCase.initialRoundOffsData(type = RoundOffType.SMS.name).collect {
                _initialRoundOffLiveData.postValue(it)
            }
        }
    }

    fun fetchRoundOffStepsData() {
        viewModelScope.launch {
            fetchRoundOffStepsUseCase.fetchRoundOffSetupSteps().collect {
                _roundOffStepsLiveData.postValue(it)
            }
        }
    }

    fun enableManualRoundOff() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.ROUND_OFFS,
                enableAutoSave = false
            ).collect {}
        }
    }
}