package com.jar.app.feature_round_off.impl.ui.pause

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PauseRoundOffViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
) : ViewModel() {
    private val _pauseOptionsLiveData =
        MutableLiveData<RestClientResult<List<PauseSavingOptionWrapper>>>()
    val pauseOptionsLiveData: LiveData<RestClientResult<List<PauseSavingOptionWrapper>>>
        get() = _pauseOptionsLiveData

    private val _roundOffsPausedLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val roundOffsLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _roundOffsPausedLiveData

    var pauseSavingOptionWrapper: PauseSavingOptionWrapper? = null

    fun fetchPauseOptions() {
        _pauseOptionsLiveData.postValue(RestClientResult.loading())
        viewModelScope.launch {
            pauseSavingOptionWrapper = PauseSavingOptionWrapper(PauseSavingOption.TWO, true)
            _pauseOptionsLiveData.postValue(
                RestClientResult.success(
                    listOf(
                        PauseSavingOptionWrapper(PauseSavingOption.TWO, true),
                        PauseSavingOptionWrapper(PauseSavingOption.EIGHT),
                        PauseSavingOptionWrapper(PauseSavingOption.TWELVE),
                        PauseSavingOptionWrapper(PauseSavingOption.FIFTEEN),
                    )
                )
            )
        }
    }

    fun updatePauseOptionListOnItemClick(list: List<PauseSavingOptionWrapper>, position: Int) {
        viewModelScope.launch(dispatcherProvider.default) {
            val newList = ArrayList(list.map { it.copy() })
            if (newList[position].isSelected) {
                newList[position].isSelected = false
                pauseSavingOptionWrapper = null
            } else {
                newList.filter { it.isSelected }.map { it.isSelected = false }
                newList[position].isSelected = true
                pauseSavingOptionWrapper = newList[position]
            }
            _pauseOptionsLiveData.postValue(RestClientResult.success(newList))
        }
    }

    fun pauseRoundOffs() {
        viewModelScope.launch {
            if (pauseSavingOptionWrapper != null) {
                pauseSavingOptionWrapper?.let {
                    updatePauseSavingUseCase.updatePauseSavingValue(
                        shouldPause = true,
                        pauseType = SavingsType.ROUND_OFFS.name,
                        pauseDuration = it.pauseSavingOption.name
                    ).collect { _roundOffsPausedLiveData.postValue(it) }
                }
            }
        }
    }
}