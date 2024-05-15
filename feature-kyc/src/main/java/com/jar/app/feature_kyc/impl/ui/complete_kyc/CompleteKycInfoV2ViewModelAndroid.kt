package com.jar.app.feature_kyc.impl.ui.complete_kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.PostPanOcrRequestUseCase
import com.jar.app.feature_kyc.shared.ui.complete_kyc.CompleteKycInfoV2ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CompleteKycInfoV2ViewModelAndroid @Inject constructor(
    private val postPanOcrRequestUseCase: PostPanOcrRequestUseCase,
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        CompleteKycInfoV2ViewModel(
            postPanOcrRequestUseCase,
            postManualKycRequestUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}