package com.jar.app.feature_lending_kyc.impl.ui.pan.loading_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchJarVerifiedUserPanUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.loading_screen.PanFromJarLoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PanFromJarLoadingViewModelAndroid @Inject constructor(
    private val fetchJarVerifiedUserPanUseCase: FetchJarVerifiedUserPanUseCase
) : ViewModel() {

    private val viewModel by lazy {
        PanFromJarLoadingViewModel(
            fetchJarVerifiedUserPanUseCase = fetchJarVerifiedUserPanUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}