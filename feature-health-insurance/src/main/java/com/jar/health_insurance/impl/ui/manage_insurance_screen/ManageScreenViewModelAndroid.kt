package com.jar.health_insurance.impl.ui.manage_insurance_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchManageScreenDataUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentConfigUseCase
import com.jar.app.feature_health_insurance.shared.ui.ManageScreenViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageScreenViewModelAndroid @Inject constructor(
    private val fetchManageScreenDataUseCase: FetchManageScreenDataUseCase,
    private val fetchPaymentConfigUseCase: FetchPaymentConfigUseCase,
    private val fetchInsuranceTransactionsUseCase: FetchInsuranceTransactionsUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {
    private val viewModel by lazy {
        ManageScreenViewModel(
            fetchManageScreenDataUseCase = fetchManageScreenDataUseCase,
            fetchPaymentConfigUseCase = fetchPaymentConfigUseCase,
            fetchInsuranceTransactionsUseCase = fetchInsuranceTransactionsUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope

        )
    }

    fun getInstance() = viewModel

}


