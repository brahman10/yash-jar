package com.jar.app.feature_lending.impl.ui.mandate.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.ui.mandate.status.MandateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MandateViewModelAndroid @Inject constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val staticContentUseCase: FetchStaticContentUseCase
) : ViewModel() {

    private val viewModel by lazy {
        MandateViewModel(
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            staticContentUseCase=staticContentUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}