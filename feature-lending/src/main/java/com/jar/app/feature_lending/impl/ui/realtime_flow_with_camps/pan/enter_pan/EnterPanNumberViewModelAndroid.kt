package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.enter_pan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.pan.enter_pan.EnterPanNumberViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class EnterPanNumberViewModelAndroid @Inject constructor(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        EnterPanNumberViewModel(
            postManualKycRequestUseCase = postManualKycRequestUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}