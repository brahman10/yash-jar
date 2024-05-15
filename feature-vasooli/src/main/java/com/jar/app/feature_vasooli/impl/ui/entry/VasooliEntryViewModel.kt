package com.jar.app.feature_vasooli.impl.ui.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.UpdateEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryResponse
import com.jar.app.feature_vasooli.impl.domain.use_case.PostVasooliRequestUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.UpdateVasooliEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliEntryViewModel @Inject constructor(
    private val postVasooliRequestUseCase: PostVasooliRequestUseCase,
    private val updateVasooliEntryUseCase: UpdateVasooliEntryUseCase
) : ViewModel() {

    private val _postVasooliRequestLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<VasooliEntryResponse>>>()
    val postVasooliRequestLiveData: LiveData<RestClientResult<ApiResponseWrapper<VasooliEntryResponse>>>
        get() = _postVasooliRequestLiveData

    private val _updateVasooliEntryLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val updateVasooliEntryLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateVasooliEntryLiveData

    fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest) {
        viewModelScope.launch {
            postVasooliRequestUseCase.postVasooliRequest(vasooliEntryRequest).collect {
                _postVasooliRequestLiveData.postValue(it)
            }
        }
    }

    fun updateVasooliEntry(updateEntryRequest: UpdateEntryRequest) {
        viewModelScope.launch {
            updateVasooliEntryUseCase.updateVasooliEntry(updateEntryRequest).collect {
                _updateVasooliEntryLiveData.postValue(it)
            }
        }
    }
}