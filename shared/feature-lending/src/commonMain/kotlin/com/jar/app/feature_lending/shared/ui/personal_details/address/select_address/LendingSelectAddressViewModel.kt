package com.jar.app.feature_lending.shared.ui.personal_details.address.select_address

import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.app.feature_lending.shared.domain.use_case.UpdateAddressDetailsUseCase
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult

class LendingSelectAddressViewModel constructor(
    private val getUserSavedAddressUseCase: GetUserSavedAddressUseCase,
    private val updateAddressDetailsUseCase: UpdateAddressDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _addressListFlow =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<Addresses>>>()
    val addressListFlow: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<Addresses>>>
        get() = _addressListFlow.toCommonFlow()

    private val _updateAddressFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LendingAddress?>>>()
    val updateAddressFlow: CFlow<RestClientResult<ApiResponseWrapper<LendingAddress?>>>
        get() = _updateAddressFlow.toCommonFlow()

    private var addressList: MutableList<Address>? = null

    var selectedAddress: Address? = null

    fun fetchAddressList() {
        viewModelScope.launch {
            getUserSavedAddressUseCase.getSavedAddress().collect {
                addressList = it.data?.data?.addresses?.toMutableList()
                _addressListFlow.emit(it)
            }
        }
    }

    fun selectAddress(address: Address) {
        viewModelScope.launch {
            selectedAddress = address
            val newList = addressList?.map {
                if (it.addressId == address.addressId) {
                    it.copy(isSelected = true)
                } else {
                    it.copy(isSelected = false)
                }
            }.orEmpty()
            _addressListFlow.emit(
                LibraryRestClientResult.success(LibraryApiResponseWrapper(Addresses(newList), true))
            )
        }
    }

    fun updateAddressDetails(lendingAddress: LendingAddress) {
        viewModelScope.launch {
            updateAddressDetailsUseCase.updateAddressDetails(lendingAddress).collect {
                _updateAddressFlow.emit(it)
            }
        }
    }
}