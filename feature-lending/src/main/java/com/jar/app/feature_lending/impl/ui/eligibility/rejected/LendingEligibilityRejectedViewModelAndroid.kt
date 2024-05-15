package com.jar.app.feature_lending.impl.ui.eligibility.rejected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.AcknowledgeOneTimeCardUseCase
import com.jar.app.feature_lending.shared.ui.eligibility.rejected.LendingEligibilityRejectedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingEligibilityRejectedViewModelAndroid @Inject constructor(
    private val acknowledgeOneTimeCardUseCase: AcknowledgeOneTimeCardUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LendingEligibilityRejectedViewModel(
            acknowledgeOneTimeCardUseCase = acknowledgeOneTimeCardUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}