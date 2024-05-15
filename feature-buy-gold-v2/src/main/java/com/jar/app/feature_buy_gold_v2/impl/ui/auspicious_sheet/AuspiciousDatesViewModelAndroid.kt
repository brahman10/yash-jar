package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousDatesUseCase
import com.jar.app.feature_buy_gold_v2.shared.ui.AuspiciousDatesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AuspiciousDatesViewModelAndroid @Inject constructor(
    private val fetchAuspiciousDatesUseCase: FetchAuspiciousDatesUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        AuspiciousDatesViewModel(
            fetchAuspiciousDatesUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}