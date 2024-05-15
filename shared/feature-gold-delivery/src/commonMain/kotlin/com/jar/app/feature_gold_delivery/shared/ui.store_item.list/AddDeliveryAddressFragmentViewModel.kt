package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AddDeliveryAddressFragmentViewModel constructor(
    private val addDeliveryAddressUseCase: AddDeliveryAddressUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _addressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Address?>>>(RestClientResult.none())
    val addressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Address?>>>
        get() = _addressLiveData.toCommonStateFlow()

    fun addAddress(address: Address) {
        viewModelScope.launch {
            addDeliveryAddressUseCase.addDeliveryAddress(address).collect {
                _addressLiveData.emit(it)
            }
        }
    }

}