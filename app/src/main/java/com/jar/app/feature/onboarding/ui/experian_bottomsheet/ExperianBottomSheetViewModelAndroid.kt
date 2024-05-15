package com.jar.app.feature.onboarding.ui.experian_bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchExperianTCUseCase
import com.jar.app.feature_onboarding.shared.ui.experian_bottomsheet.ExperianBottomSheetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ExperianBottomSheetViewModelAndroid @Inject constructor(
    fetchExperianTCUseCase: FetchExperianTCUseCase
) : ViewModel() {

    private val viewModel by lazy {
        ExperianBottomSheetViewModel(
            fetchExperianTCUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}