package com.jar.app.feature_gold_sip.impl.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipIntroUseCase
import com.jar.app.feature_gold_sip.shared.ui.GoldSipIntroViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class GoldSipIntroViewModelAndroid @Inject constructor(
    private val fetchGoldSipIntroUseCase: FetchGoldSipIntroUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        GoldSipIntroViewModel(
            fetchGoldSipIntroUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}