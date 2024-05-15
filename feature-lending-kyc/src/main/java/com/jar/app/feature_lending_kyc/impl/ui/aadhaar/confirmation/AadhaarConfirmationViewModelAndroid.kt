package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SaveAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.confirmation.AadhaarConfirmationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AadhaarConfirmationViewModelAndroid @Inject constructor(
    private val saveAadhaarDetailsUseCase: SaveAadhaarDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        AadhaarConfirmationViewModel(
            saveAadhaarDetailsUseCase = saveAadhaarDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}