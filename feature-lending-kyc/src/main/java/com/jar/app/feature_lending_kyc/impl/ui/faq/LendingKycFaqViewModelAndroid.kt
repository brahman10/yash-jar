package com.jar.app.feature_lending_kyc.impl.ui.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqListUseCase
import com.jar.app.feature_lending_kyc.shared.ui.faq.LendingKycFaqViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingKycFaqViewModelAndroid @Inject constructor(
    private val fetchLendingKycFaqListUseCase: FetchLendingKycFaqListUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingKycFaqViewModel(
            fetchLendingKycFaqListUseCase = fetchLendingKycFaqListUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}