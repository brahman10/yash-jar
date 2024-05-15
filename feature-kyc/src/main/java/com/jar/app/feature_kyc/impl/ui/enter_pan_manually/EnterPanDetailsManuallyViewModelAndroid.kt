package com.jar.app.feature_kyc.impl.ui.enter_pan_manually

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_kyc.shared.ui.enter_pan_manually.EnterPanDetailsManuallyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EnterPanDetailsManuallyViewModelAndroid @Inject constructor(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        EnterPanDetailsManuallyViewModel(
            postManualKycRequestUseCase = postManualKycRequestUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}