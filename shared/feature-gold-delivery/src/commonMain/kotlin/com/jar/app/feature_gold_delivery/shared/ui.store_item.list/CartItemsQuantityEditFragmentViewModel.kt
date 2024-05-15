package  com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AvailableVolumeV2
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CartItemsQuantityEditQuantityEditFragmentViewModel   constructor(
    private val deleteAddressUseCase: DeleteItemToCartUseCase,
    private val productWishlistUseCase: AddProductToWishlistUseCase,
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val removeProductFromWishlistUseCase: RemoveProductFromWishlistUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    val currentProduct: MutableStateFlow<ProductV2?> = MutableStateFlow<ProductV2?>(null)


    private val _addItemsToCart =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val addItemsToCart: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _addItemsToCart.toCommonFlow()

    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonFlow()

    private val _currentSelectedVolumeIndex =
        MutableStateFlow<Int>(0)
    val currentSelectedVolumeIndex: CStateFlow<Int>
        get() = _currentSelectedVolumeIndex.toCommonStateFlow()

    private val _deleteAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteAddressLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonFlow()

    private val _showToast = MutableStateFlow<String>("")
    val showToast: CFlow<String>
        get() = _showToast.toCommonFlow()

    private val _isProductLiked =
        MutableStateFlow<Boolean>(false)
    val isProductLiked: CFlow<Boolean>
        get() = _isProductLiked.toCommonFlow()

    fun addToWishList(
        addCartItemRequest: AddCartItemRequest,
        currentAvailableVolume: AvailableVolumeV2?
    ) {
        viewModelScope.launch {
            productWishlistUseCase.addProductToWishlist(addCartItemRequest).collect {
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    _showToast.value = "Product added to wishlist"
                    currentAvailableVolume?.wishListId = it.data?.data?.id
                    _isProductLiked.value = true
                } else if (it.status != RestClientResult.Status.LOADING) {
                    it.data?.errorMessage?.let {
                        _showToast.value = it
                    } ?: run {
                        _showToast.value = "Something went wrong"
                    }
                }
            }
        }
    }

    fun setCurrentVolumeIndex(it: Int) {
        viewModelScope.launch {
            _currentSelectedVolumeIndex.emit(it)
        }
    }

    fun removeFromWishList(addCartItemRequest: String, currentAvailableVolume: AvailableVolumeV2?) {
        viewModelScope.launch {
            removeProductFromWishlistUseCase.removeProductFromWishlist(addCartItemRequest).collect {
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    _isProductLiked.value = false
                    _showToast.value = "Product removed from wishlist"
                } else if (it.status != RestClientResult.Status.LOADING) {
                    it.data?.errorMessage?.let {
                        _showToast.value = it
                    } ?: run {
                        _showToast.value = "Something went wrong"
                    }
                }
            }
        }
    }

    fun getAllStoreItems() {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(null).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }

    fun setCurrentProduct(product: ProductV2, index: Int) {
        currentProduct.value = product
        _currentSelectedVolumeIndex.value = index
    }

    fun replaceItemFromCart(toBeDeleted: String, addCartItemRequest: AddCartItemRequest) {
        viewModelScope.launch {
            deleteAddressUseCase.deleteItemFromCart(toBeDeleted).collect {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    addItemToCart(addCartItemRequest)
                }
            }
        }
    }

    fun addItemToCart(addCartItemRequest: AddCartItemRequest) {
        viewModelScope.launch {
            updateCartUseCase.addItemToCart(addCartItemRequest).collect {
                _addItemsToCart.value = it
            }
        }
    }
}