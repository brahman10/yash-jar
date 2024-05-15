package com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchExperianReportUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeCreditDetailsUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow.bottom_sheet.RealTimeRefreshCreditScoreBottomSheetViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RealTimeRefreshCreditScoreBottomSheetViewModelAndroid @Inject constructor(
    private val fetchRealTimeCreditDetailsUseCase: FetchRealTimeCreditDetailsUseCase,
    private val fetchExperianReportUseCase: FetchExperianReportUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {


    private val viewModel by lazy {
        RealTimeRefreshCreditScoreBottomSheetViewModel(
            fetchRealTimeCreditDetailsUseCase = fetchRealTimeCreditDetailsUseCase,
            fetchExperianReportUseCase = fetchExperianReportUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}