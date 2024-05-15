package com.jar.app.feature_lending_kyc.impl.ui.faq.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.ui.faq.details.LendingKycFaqDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingKycFaqDetailsViewModelAndroid @Inject constructor(
    private val fetchLendingKycFaqDetailsUseCase: FetchLendingKycFaqDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingKycFaqDetailsViewModel(
            fetchLendingKycFaqDetailsUseCase = fetchLendingKycFaqDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}