package com.jar.app.feature_lending.impl.ui.foreclosure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.foreclosure.ForeclosureSuccessViewModel
import com.jar.app.feature_lending.shared.ui.host_container.LendingHostViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ForeclosureSuccessViewModelAndroid @Inject constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
) : ViewModel() {

    private val viewModel by lazy {
        ForeclosureSuccessViewModel(
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}