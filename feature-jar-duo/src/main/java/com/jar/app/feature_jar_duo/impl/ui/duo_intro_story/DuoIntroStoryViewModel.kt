package com.jar.app.feature_jar_duo.impl.ui.duo_intro_story

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroData
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroStoryResponse
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchDuoIntroStoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DuoIntroStoryViewModel @Inject constructor(
    private val mApp: Application,
    private val fetchDuoIntroStoryUseCase: FetchDuoIntroStoryUseCase
) : ViewModel() {

    private val _introStoryLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DuoIntroData>>>()
    val introStoryLiveData: LiveData<RestClientResult<ApiResponseWrapper<DuoIntroData>>>
        get() = _introStoryLiveData

    fun getIntroStoryData() {
        viewModelScope.launch {
            fetchDuoIntroStoryUseCase.fetchDuoIntroStory().collectLatest {
                _introStoryLiveData.postValue(it)
            }
        }
    }
}