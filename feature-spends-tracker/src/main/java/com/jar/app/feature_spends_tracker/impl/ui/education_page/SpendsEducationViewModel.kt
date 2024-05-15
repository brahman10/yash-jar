package com.jar.app.feature_spends_tracker.impl.ui.education_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsEducationDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SpendsEducationViewModel @Inject constructor(private val fetchSpendsEducationDataUseCase: FetchSpendsEducationDataUseCase) :
    ViewModel() {
    private val _spendsEducationDataLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SpendsEducationData?>>>()
    val spendsEducationDataLiveData: LiveData<RestClientResult<ApiResponseWrapper<SpendsEducationData?>>>
        get() = _spendsEducationDataLiveData

    fun fetchSpendsEducationData() {
        viewModelScope.launch {
            fetchSpendsEducationDataUseCase.fetchSpendsEducationData()
                .collectLatest { educationData ->
                    _spendsEducationDataLiveData.postValue(educationData)
                }
        }
    }

}