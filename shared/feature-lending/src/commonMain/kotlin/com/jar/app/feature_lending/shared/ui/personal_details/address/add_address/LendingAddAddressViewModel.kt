package com.jar.app.feature_lending.shared.ui.personal_details.address.add_address

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.use_case.AddUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.EditUserAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingAddAddressViewModel constructor(
    private val addUserAddressUseCase: AddUserAddressUseCase,
    private val editUserAddressUseCase: EditUserAddressUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _addressFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<Address>>>(RestClientResult.none())
    val addressFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Address>>>
        get() = _addressFlow.toCommonStateFlow()

    private val _editAddressFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Address>>>(RestClientResult.none())
    val editAddressFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Address>>>
        get() = _editAddressFlow.toCommonStateFlow()

    fun editAddress(id: String, address: Address) {
        viewModelScope.launch {
            editUserAddressUseCase.editAddress(id, address).collect {
                _editAddressFlow.emit(it)
            }
        }
    }

    fun addAddress(address: Address) {
        viewModelScope.launch {
            addUserAddressUseCase.addDeliveryAddress(address).collect {
                _addressFlow.emit(it)
            }
        }
    }

}