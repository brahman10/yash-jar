package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt

import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class ActionPromptViewModel constructor(
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _editDetailFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>()
    val editDetailFlow: CFlow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>
        get() = _editDetailFlow.toCommonFlow()


    fun extractEditDetail(progressResponse: KycProgressResponse, type: Int) {
        viewModelScope.launch {
            when (type) {
                DOCUMENT_AADHAAR -> {
                    progressResponse.kycProgress?.AADHAAR?.let {
                        _editDetailFlow.emit(
                            RestClientResult.success(
                                ApiResponseWrapper(
                                    KycAadhaar(it.aadhaarNo, it.dob, it.name),
                                    true
                                )
                            )
                        )
                    }
                }

                DOCUMENT_PAN -> {
                    progressResponse.kycProgress?.PAN?.let {
                        _editDetailFlow.emit(
                            RestClientResult.success(
                                ApiResponseWrapper(
                                    KycAadhaar(
                                        it.panNo,
                                        it.dob,
                                        it.firstName.orEmpty() + it.lastName.orEmpty(),
                                        jarVerifiedPAN = it.jarVerifiedPAN
                                    ), true
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val DOCUMENT_PAN = 1
        const val DOCUMENT_AADHAAR = 2
    }
}