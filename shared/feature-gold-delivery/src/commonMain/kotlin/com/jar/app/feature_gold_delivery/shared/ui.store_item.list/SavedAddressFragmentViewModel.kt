package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetSavedAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.launch

class SavedAddressFragmentViewModel  constructor(
    private val getSavedAddressUseCase: GetSavedAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _currentlySelected = MutableStateFlow<Int>(-1)
    val currentlySelected: CStateFlow<Int>
        get() = _currentlySelected.toCommonStateFlow()

    private val _savedAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Addresses?>>>(RestClientResult.none())
    val savedAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Addresses?>>>
        get() = _savedAddressLiveData.toCommonStateFlow()

    private val _deleteAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonStateFlow()

    fun getSavedAddress(s: String? = null) {
        viewModelScope.launch {
            getSavedAddressUseCase.getSavedAddress().collect {
                _savedAddressLiveData.emit(it)
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    val indexOfFirst = it.data?.data?.addresses?.indexOfFirst { s == it.addressId }
                    indexOfFirst?.let {
                        _currentlySelected.value = it
                    }
                }
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            deleteAddressUseCase.deleteAddress(id).collect {
                _deleteAddressLiveData.emit(it)
            }
        }
    }

    fun setIndex(it: Int) {
        _currentlySelected.value = it
    }
}