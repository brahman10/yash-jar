package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.ui.GoldSipAutoPaySuccessViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldSipAutoPaySuccessViewModelAndroid @Inject constructor(
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {


    private val viewModel by lazy {
        GoldSipAutoPaySuccessViewModel(
            fetchGoldSipTypeSetupInfoUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}