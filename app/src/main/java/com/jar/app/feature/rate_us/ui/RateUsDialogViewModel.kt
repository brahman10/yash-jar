package com.jar.app.feature.rate_us.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.usecase.UpdateUserRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
internal class RateUsDialogViewModel @Inject constructor(
    private val updateUserRatingUseCase: UpdateUserRatingUseCase
): ViewModel() {

    private val _updateUserRatingLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<String>>>()
    val updateUserRatingLiveData: LiveData<RestClientResult<ApiResponseWrapper<String>>>
        get() = _updateUserRatingLiveData

    fun submitUserRating(jsonElement: JsonObject){
        viewModelScope.launch {
            updateUserRatingUseCase.submitUserRating(jsonElement).collect{
                _updateUserRatingLiveData.postValue(it)
            }
        }
    }
}