package com.jar.app.feature_lending_kyc.impl.ui.digilocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.UpdateDigiLockerRedirectionDataUseCase
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.digilocker.DigiLockerWebViewViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DigiLockerWebViewViewModelAndroid @Inject constructor(
    private val digiLockerVerificationStatusUseCase: FetchDigiLockerVerificationStatusUseCase,
    private val updateDigiLockerRedirectionDataUseCase: UpdateDigiLockerRedirectionDataUseCase
) : ViewModel() {
    private val viewModel by lazy {
        DigiLockerWebViewViewModel(
            digiLockerVerificationStatusUseCase = digiLockerVerificationStatusUseCase,
            updateDigiLockerRedirectionDataUseCase = updateDigiLockerRedirectionDataUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}