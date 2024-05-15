package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class BaseAddressFragmentViewModel constructor(
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _validatePinCodeLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>()
    val validatePinCodeLiveData: CFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>
        get() = _validatePinCodeLiveData.toCommonFlow()

    fun validatePinCode(pinCode: String) {
        viewModelScope.launch {
            validatePinCodeUseCase.validatePinCode(pinCode).collect {
                _validatePinCodeLiveData.emit(it)
            }
        }
    }
}