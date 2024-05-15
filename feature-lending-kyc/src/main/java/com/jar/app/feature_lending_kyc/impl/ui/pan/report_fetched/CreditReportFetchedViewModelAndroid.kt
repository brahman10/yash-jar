package com.jar.app.feature_lending_kyc.impl.ui.pan.report_fetched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchVerifyAadhaarPanLinkageUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SavePanDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.ui.pan.report_fetched.CreditReportFetchedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CreditReportFetchedViewModelAndroid @Inject constructor(
    private val savePanDetailsUseCase: SavePanDetailsUseCase,
    private val fetchVerifyAadhaarPanLinkageUseCase: FetchVerifyAadhaarPanLinkageUseCase
) : ViewModel() {

    private val viewModel by lazy {
        CreditReportFetchedViewModel(
            savePanDetailsUseCase = savePanDetailsUseCase,
            fetchVerifyAadhaarPanLinkageUseCase = fetchVerifyAadhaarPanLinkageUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}