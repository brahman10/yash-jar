package com.jar.app.feature_lending_kyc.impl.ui.pan.loading_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyPanDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.loading_screen.VerifyPanDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class VerifyPanDetailsViewModelAndroid @Inject constructor(
    private val verifyPanDetailsUseCase: VerifyPanDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        VerifyPanDetailsViewModel(
            verifyPanDetailsUseCase = verifyPanDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}