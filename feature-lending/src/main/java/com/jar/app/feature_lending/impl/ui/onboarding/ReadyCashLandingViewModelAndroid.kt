package com.jar.app.feature_lending.impl.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashLandingScreenContentUseCase
import com.jar.app.feature_lending.shared.ui.onboarding.ReadyCashLandingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ReadyCashLandingViewModelAndroid @Inject constructor(
    private val fetchReadyCashLandingScreenContent: FetchReadyCashLandingScreenContentUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        ReadyCashLandingViewModel(
            fetchReadyCashLandingScreenContent = fetchReadyCashLandingScreenContent,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}