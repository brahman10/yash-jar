package com.jar.app.feature_lending_kyc.impl.ui.selfie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifySelfieUseCase
import com.jar.app.feature_lending_kyc.shared.ui.selfie.SelfieCheckViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
internal class SelfieCheckViewModelAndroid @Inject constructor(
    private val verifySelfieUseCase: VerifySelfieUseCase
) : ViewModel() {

    private val viewModel by lazy {
        SelfieCheckViewModel(
            verifySelfieUseCase = verifySelfieUseCase,
            coroutineScope = viewModelScope
        )
    }
    fun getInstance() = viewModel
}