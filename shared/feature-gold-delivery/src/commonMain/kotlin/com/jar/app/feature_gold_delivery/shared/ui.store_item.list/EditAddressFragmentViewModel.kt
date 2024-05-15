package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.launch

class EditAddressFragmentViewModel constructor(
    private val editAddressUseCase: EditAddressUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _editAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Address?>>>(RestClientResult.none())
    val editAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Address?>>>
        get() = _editAddressLiveData.toCommonStateFlow()

    fun editAddress(id: String, address: Address) {
        viewModelScope.launch {
            editAddressUseCase.editAddress(id, address).collect {
                _editAddressLiveData.emit(it)
            }
        }
    }
}