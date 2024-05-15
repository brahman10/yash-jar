package com.jar.app.feature_lending_kyc.shared.ui.pan.report_fetched

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchVerifyAadhaarPanLinkageUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SavePanDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class CreditReportFetchedViewModel constructor(
    private val savePanDetailsUseCase: SavePanDetailsUseCase,
    private val fetchVerifyAadhaarPanLinkageUseCase: FetchVerifyAadhaarPanLinkageUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _savePanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val savePanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _savePanDetailsFlow.toCommonStateFlow()

    private val _aadhaarPanLinkageFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val aadhaarPanLinkageFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _aadhaarPanLinkageFlow.toCommonStateFlow()

    fun savePanDetails(creditReportPAN: CreditReportPAN, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            savePanDetailsUseCase.savePanDetails(
                JsonObject(
                    mapOf(
                        Pair("panNumber", JsonPrimitive(creditReportPAN.panNumber)),
                        Pair("dob", JsonPrimitive(creditReportPAN.dob)),
                        Pair("firstName", JsonPrimitive(creditReportPAN.firstName)),
                        Pair("lastName", JsonPrimitive(creditReportPAN.lastName))
                    )
                ),
                kycFeatureFlowType
            ).collect {
                _savePanDetailsFlow.emit(it)
            }
        }
    }

    fun verifyAadhaarPanLinkage(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            fetchVerifyAadhaarPanLinkageUseCase.verifyAadhaarPanLinkage(kycFeatureFlowType).collect {
                _aadhaarPanLinkageFlow.emit(it)
            }
        }
    }
}