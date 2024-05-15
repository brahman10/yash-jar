package com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.GoldSipTypeSelectionViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldSipTypeSelectionViewModelAndroid @Inject constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        GoldSipTypeSelectionViewModel(
            updateGoldSipDetailsUseCase,
            fetchGoldSipTypeSetupInfoUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}