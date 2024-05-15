package com.jar.app.feature_lending.impl.ui.downtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateNotifyUserUseCase
import com.jar.app.feature_lending.shared.ui.downtime.PartnerDownTimeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PartnerDownTimeViewModelAndroid @Inject constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val updateNotifyUserUseCase: UpdateNotifyUserUseCase
) : ViewModel() {

    private val viewModel by lazy {
        PartnerDownTimeViewModel(
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            updateNotifyUserUseCase = updateNotifyUserUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}