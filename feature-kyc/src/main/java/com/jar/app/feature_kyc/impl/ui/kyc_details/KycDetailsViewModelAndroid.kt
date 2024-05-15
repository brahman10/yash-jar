package com.jar.app.feature_kyc.impl.ui.kyc_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDetailsUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycFaqUseCase
import com.jar.app.feature_kyc.shared.ui.kyc_details.KycDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class KycDetailsViewModelAndroid @Inject constructor(
    private val fetchKycDetailsUseCase: FetchKycDetailsUseCase,
    private val fetchKycFaqUseCase: FetchKycFaqUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        KycDetailsViewModel(
            fetchKycDetailsUseCase = fetchKycDetailsUseCase,
            fetchKycFaqUseCase = fetchKycFaqUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}