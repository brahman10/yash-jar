package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliveryLandingData
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetDeliveryLandingDetailsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DeliveryStoreItemListFragmentViewModel constructor(
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val getDeliveryLandingDetailsUseCase: GetDeliveryLandingDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var lastCategoryTitle: String? = null
    private val _showToast = MutableSharedFlow<String>()
    val showToast: CFlow<String>
        get() = _showToast.toCommonFlow()

    private val _fetchCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>(RestClientResult.none())
    val fetchCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>
        get() = _fetchCartLiveData.toCommonStateFlow()

    private val _storeItemsLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>()
    val storeItemsLiveData: CFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonFlow()

    private val _otherProductsRv =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val otherProductsRv: CStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _otherProductsRv.toCommonStateFlow()


    private val _getLandingDetails =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DeliveryLandingData?>>>(
            RestClientResult.none()
        )
    val getLandingDetails: CStateFlow<RestClientResult<ApiResponseWrapper<DeliveryLandingData?>>>
        get() = _getLandingDetails.toCommonStateFlow()


    private val _validatePinCodeLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>()
    val validatePinCodeLiveData: CFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>
        get() = _validatePinCodeLiveData.toCommonFlow()



    fun getAllStoreItems(category: String? = null) {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(category).collect {
                _storeItemsLiveData.emit(it)
                if (_otherProductsRv.value.data?.data?.products.isNullOrEmpty()) {
                    _otherProductsRv.emit(it)
                }

            }
        }
    }

    fun getDeliveryLandingScreenDetails() {
        viewModelScope.launch {
            getDeliveryLandingDetailsUseCase.getDeliveryLandingScreenDetails().collect {
                _getLandingDetails.emit(it)
            }
        }
    }

    fun validatePinCode(pinCode: String) {
        viewModelScope.launch {
            validatePinCodeUseCase.validatePinCode(pinCode).collect {
                _validatePinCodeLiveData.emit(it)
            }
        }
    }

    fun clearPincode() {
    }

    fun fetchCartItems() {
        viewModelScope.launch {
            fetchCartUseCase.fetchCart().collect {
                _fetchCartLiveData.emit(it)
            }
        }
    }
}