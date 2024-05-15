package com.jar.app.feature_kyc.impl.ui.kyc_faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycFaqUseCase
import com.jar.app.feature_kyc.shared.ui.kyc_faq.KycFaqViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class KycFaqViewModelAndroid @Inject constructor(
    private val fetchKycFaqUseCase: FetchKycFaqUseCase
    ): ViewModel() {

    private val viewModel by lazy {
        KycFaqViewModel(
            fetchKycFaqUseCase = fetchKycFaqUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}