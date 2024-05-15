package com.jar.app.feature_lending_kyc.impl.ui.kyc_verified

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.shared.ui.kyc_verified.LendingKycVerifiedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingKycVerifiedViewModelAndroid @Inject constructor(
    private val fetchKycProgressUseCase: FetchKycProgressUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingKycVerifiedViewModel(
            fetchKycProgressUseCase = fetchKycProgressUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}