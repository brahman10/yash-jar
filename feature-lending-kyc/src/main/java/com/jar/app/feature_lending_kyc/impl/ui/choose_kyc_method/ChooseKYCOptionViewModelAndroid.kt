package com.jar.app.feature_lending_kyc.impl.ui.choose_kyc_method

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerRedirectionUrlUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerScreenContentUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.digilocker.ChooseKYCOptionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ChooseKYCOptionViewModelAndroid @Inject constructor(
    private val digiLockerScreenContentUseCase: FetchDigiLockerScreenContentUseCase,
    private val digiLockerRedirectionUrlUseCase: FetchDigiLockerRedirectionUrlUseCase,
    private val digiLockerVerificationStatusUseCase: FetchDigiLockerVerificationStatusUseCase

) : ViewModel() {
    private val viewModel by lazy {
        ChooseKYCOptionViewModel(
            digiLockerScreenContentUseCase = digiLockerScreenContentUseCase,
            digiLockerRedirectionUrlUseCase = digiLockerRedirectionUrlUseCase,
            digiLockerVerificationStatusUseCase = digiLockerVerificationStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}