package com.jar.app.feature_goal_based_saving.impl.ui.introduction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalSavingsIntoPage
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchIntroDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class IntroductionFragmentViewModel @Inject constructor(
    private val fetchIntroPageUseCase: FetchIntroDetailsUseCase
) : ViewModel()  {
    private val _into = MutableLiveData<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>()
    val intro: LiveData<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>
        get() = _into
    fun fetch() {
        viewModelScope.launch {
            val data = fetchIntroPageUseCase.execute()
            data.collect() {
                _into.postValue(it)
            }
        }
    }
}