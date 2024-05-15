package com.jar.app.feature_lending.impl.ui.eligibility.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.ui.eligibility.loading.LendingEligibilityLoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingEligibilityLoadingViewModelAndroid @Inject constructor(
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingEligibilityLoadingViewModel(
            fetchLendingV2PreApprovedDataUseCase = fetchLendingV2PreApprovedDataUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}