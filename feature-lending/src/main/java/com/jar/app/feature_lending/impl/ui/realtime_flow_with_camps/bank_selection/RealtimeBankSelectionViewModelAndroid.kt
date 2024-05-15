package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.bank_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsBanksUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsSdkRedirectDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ScheduleBankUptimeNotificationUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.bank_selection.RealtimeBankSelectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RealtimeBankSelectionViewModelAndroid @Inject constructor(
    private val fetchCamsBanksUseCase: FetchCamsBanksUseCase,
    private val scheduleBankUptimeNotificationUseCase: ScheduleBankUptimeNotificationUseCase,
    private val fetchCamsSdkRedirectDataUseCase: FetchCamsSdkRedirectDataUseCase
) : ViewModel() {

    private val viewModel by lazy {
        RealtimeBankSelectionViewModel(
            fetchCamsBanksUseCase = fetchCamsBanksUseCase,
            scheduleBankUptimeNotificationUseCase = scheduleBankUptimeNotificationUseCase,
            fetchCamsSdkRedirectDataUseCase = fetchCamsSdkRedirectDataUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}