package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIBreakdownData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartBreakdownUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class StoreCartFragmentViewModel   constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val deleteItemFromCart: DeleteItemToCartUseCase,
    private val deleteAddressUseCase: DeleteItemToCartUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val editItemQuantityCart: EditItemQuantityCartUseCase,
    private val fetchCartBreakdownUseCase: FetchCartBreakdownUseCase,
    private val goldDeliveryPaymentUseCase: PostOrderUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _validatePinCodeLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>(RestClientResult.none())
    val validatePinCodeLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>
        get() = _validatePinCodeLiveData.toCommonStateFlow()

    fun validatePinCode(pinCode: String) {
        viewModelScope.launch {
            validatePinCodeUseCase.validatePinCode(pinCode).collect {
                _validatePinCodeLiveData.emit(it)
            }
        }
    }
    var placeOrderRequest: GoldDeliveryPlaceOrderDataRequest? = null

    var navigateToCheckOutOnDeliveryAddressChange: Boolean = false
    private val _currentCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>(RestClientResult.none())
    val currentCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>
        get() = _currentCartLiveData.toCommonStateFlow()

    private val _selectedAddress =
        MutableStateFlow<Address?>(null)
    val selectedAddress: CStateFlow<Address?>
        get() = _selectedAddress.toCommonStateFlow()

    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonStateFlow()

    private val _deleteAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonStateFlow()

    private val _deleteItemFromcartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteItemFromcartLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteItemFromcartLiveData.toCommonFlow()

    private val _currentCartBreakdownLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CartAPIBreakdownData?>>>(RestClientResult.none())
    val currentCartBreakdownLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CartAPIBreakdownData?>>>
        get() = _currentCartBreakdownLiveData.toCommonStateFlow()

    private val _placeOrderAPILiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val placeOrderAPILiveData: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _placeOrderAPILiveData.toCommonFlow()

    private val _buyPriceLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>(RestClientResult.none())
    val buyPriceLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _buyPriceLiveData.toCommonStateFlow()

    fun removeFromCart(id: String, quantity: Int) {
        viewModelScope.launch {
            val async = async { editItemQuantityCart.changeQuantityInCart("", 2) }
            val await = async.await()
        }
        if (quantity == 0) {
            removeFromCart(id)
            return
        }
        viewModelScope.launch {
            editItemQuantityCart.changeQuantityInCart(id, quantity).collectLatest {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    fetchNewCart()
                    fetchCartBreakdown()
                }
            }
        }
    }

    fun removeFromCart(id: String) {
        viewModelScope.launch {
            deleteItemFromCart.deleteItemFromCart(id).collectLatest {
                _deleteItemFromcartLiveData.value = it
            }
        }
    }

    fun fetchNewCart() {
        viewModelScope.launch {
            fetchCartUseCase.fetchCart().collectLatest {
                _currentCartLiveData.emit(it)
            }
        }
    }

    fun fetchCartBreakdown() {
        viewModelScope.launch {
            fetchCartBreakdownUseCase.fetchCartBreakdown().collectLatest {
                _currentCartBreakdownLiveData.emit(it)
            }
        }
    }

    fun placeGoldDeliveryOrder(goldDeliveryPlaceOrderDataRequest: GoldDeliveryPlaceOrderDataRequest) {
        viewModelScope.launch {
            goldDeliveryPaymentUseCase.postOrder(goldDeliveryPlaceOrderDataRequest).collectLatest {
                _placeOrderAPILiveData.emit(it)
            }
        }
    }

    fun fetchCurrentBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectLatest {
                _buyPriceLiveData.emit(it)
            }
        }
    }

    fun deleteCartItem(it1: String) {
        viewModelScope.launch {
            deleteAddressUseCase.deleteItemFromCart(it1).collect {
                _deleteAddressLiveData.emit(it)
            }
        }

    }

    fun setSelectedAddress(it: Address) {
        viewModelScope.launch {
            _selectedAddress.emit(it)
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(null).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }

    fun clearPinCode() {
        viewModelScope.launch {
            _validatePinCodeLiveData.emit(RestClientResult.loading())
        }
    }

    fun clearAddress() {
        viewModelScope.launch {
            _selectedAddress.emit(null)
        }
    }
}