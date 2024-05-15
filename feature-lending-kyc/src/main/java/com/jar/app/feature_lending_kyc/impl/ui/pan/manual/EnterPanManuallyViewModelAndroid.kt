package com.jar.app.feature_lending_kyc.impl.ui.pan.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.manual.EnterPanManuallyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EnterPanManuallyViewModelAndroid @Inject constructor(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        EnterPanManuallyViewModel(
            postManualKycRequestUseCase = postManualKycRequestUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}