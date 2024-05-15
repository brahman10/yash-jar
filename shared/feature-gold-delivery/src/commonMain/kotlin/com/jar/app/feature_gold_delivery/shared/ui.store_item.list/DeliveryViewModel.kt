package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class DeliveryViewModel constructor(
    private val addDeliveryAddressUseCase: AddDeliveryAddressUseCase,
    coroutineScope: CoroutineScope?
) {
    fun updateCart(unit: Unit) {
        viewModelScope.launch {
            _cartUpdate.emit(!_cartUpdate.value)
        }
    }

    fun selectAddress(address: Address?) {
        viewModelScope.launch {
            _selectedAddressLiveData.emit(address)
        }
    }

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _selectedAddressLiveData = MutableStateFlow<Address?>(null)
    val selectedAddressLiveData: CStateFlow<Address?>
        get() = _selectedAddressLiveData.toCommonStateFlow()

    private val _cartUpdate = MutableStateFlow<Boolean>(false)
    val cartUpdate: CStateFlow<Boolean>
        get() = _cartUpdate.toCommonStateFlow()

    private val _refreshAddressAction = MutableStateFlow<String>("")
    val refreshAddressAction: SharedFlow<String>
        get() = _refreshAddressAction.toCommonStateFlow()

    fun updateRefreshAdress(addressId: String) {
        viewModelScope.launch {
            _refreshAddressAction.emit(addressId)
        }
    }
}