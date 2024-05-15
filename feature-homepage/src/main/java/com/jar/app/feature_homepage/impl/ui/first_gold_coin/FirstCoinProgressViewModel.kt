package com.jar.app.feature_homepage.impl.ui.first_gold_coin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinOnboardingStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FirstCoinProgressViewModel @Inject constructor(
    private val fetchFirstCoinProgressUseCase: FetchFirstCoinProgressUseCase,
): ViewModel(
) {
    private val _firstCoinProgressLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData>>>()
    val firstCoinProgressLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData>>> get() = _firstCoinProgressLiveData

    var apiResponseCount = 0

    fun fetchTransitionPageData() {
        viewModelScope.launch {
            fetchFirstCoinProgressUseCase.fetchFirstCoinProgress().collect {
                _firstCoinProgressLiveData.postValue(it)
            }
        }
    }

}