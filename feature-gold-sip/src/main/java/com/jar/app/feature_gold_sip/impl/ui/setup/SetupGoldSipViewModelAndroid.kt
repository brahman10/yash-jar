package com.jar.app.feature_gold_sip.impl.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.ui.SetupGoldSipViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SetupGoldSipViewModelAndroid @Inject constructor(
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        SetupGoldSipViewModel(
            fetchGoldSipDetailsUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}