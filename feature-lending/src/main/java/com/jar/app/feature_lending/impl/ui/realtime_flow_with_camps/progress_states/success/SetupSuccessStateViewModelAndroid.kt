package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.progress_states.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchPANStatusUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.progress_states.success.SetupSuccessStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SetupSuccessStateViewModelAndroid @Inject constructor(
    private val fetchPANStatusUseCase: FetchPANStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        SetupSuccessStateViewModel(
            fetchPANStatusUseCase = fetchPANStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}