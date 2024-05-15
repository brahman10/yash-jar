package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.feature_gold_delivery.shared.MR
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AvailableVolumeV2
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.GenericFAQ
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetStoreItemFaqUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.NotifyUserUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoreItemDetailFragmentViewModel  constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val deleteItemFromCart: DeleteItemToCartUseCase,
    private val editItemQuantityCart: EditItemQuantityCartUseCase,
    private val productWishlistUseCase: AddProductToWishlistUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val removeProductFromWishlistUseCase: RemoveProductFromWishlistUseCase,
    private val notifyUserUseCase: NotifyUserUseCase,
    private val getStoreItemFaqUseCase: GetStoreItemFaqUseCase,
    coroutineScope: CoroutineScope?
)  {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
   
    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonFlow()

    private val _buyPriceLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>(RestClientResult.none())
    val buyPriceLiveData: CFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _buyPriceLiveData.toCommonFlow()

    private val _currentCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>(RestClientResult.none())
    val currentCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>
        get() = _currentCartLiveData.toCommonStateFlow()

    private val _notifyLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val notifyLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _notifyLiveData.toCommonFlow()

    fun getAllStoreItems(category: String? = null) {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(category).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }

    private val _currentSelectedVolumeIndex =
        MutableStateFlow<Int>(0)
    val currentSelectedVolumeIndex: CStateFlow<Int>
        get() = _currentSelectedVolumeIndex.toCommonStateFlow()

    private val _isProductLiked =
        MutableStateFlow<Boolean>(false)
    val isProductLiked: CFlow<Boolean>
        get() = _isProductLiked.toCommonFlow()

    private val _isProceedBtnEnabled =
        MutableStateFlow<Boolean>(false)
    val isProceedBtnEnabled: CFlow<Boolean>
        get() = _isProceedBtnEnabled.toCommonFlow()

    private val _validatePinCodeLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>(RestClientResult.none())
    val validatePinCodeLiveData: CFlow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>
        get() = _validatePinCodeLiveData.toCommonFlow()

    private val _showToast = MutableStateFlow<String>("")
    val showToast: CFlow<String>
        get() = _showToast.toCommonFlow()

    private val _moreInfoLiveData = MutableStateFlow<List<ExpandableDataItem>>(listOf())
    val moreInfoLiveData: CStateFlow<List<ExpandableDataItem>>
        get() = _moreInfoLiveData.toCommonStateFlow()

    private val _faqLiveData = MutableStateFlow<List<ExpandableDataItem>>(listOf())
    val faqLiveData: CStateFlow<List<ExpandableDataItem>>
        get() = _faqLiveData.toCommonStateFlow()

    fun fetchCurrentBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectLatest {
                _buyPriceLiveData.emit(it)
            }
        }
    }
    fun setCurrentVolumeIndex(it: Int) {
        viewModelScope.launch {
            _currentSelectedVolumeIndex.emit(it)
        }
    }

    fun addToCart(addItemCartItemRequest: AddCartItemRequest) {
        viewModelScope.launch {
            updateCartUseCase.addItemToCart(addItemCartItemRequest).collectLatest {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    fetchNewCart()
                } else {
                    if (!it.message.isNullOrBlank()) {
                        _showToast.value = it.message!!
                    }
                }
            }
        }
    }

    fun removeFromCart(id: String, quantity: Int) {
        if (quantity == 0) {
            removeFromCart(id)
            return
        }
        viewModelScope.launch {
            editItemQuantityCart.changeQuantityInCart(id, quantity).collectLatest {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    fetchNewCart()
                } else {
                    if (!it.message.isNullOrBlank()) {
                        _showToast.value = it.message!!
                    }
                }
            }
        }
    }

    fun removeFromCart(id: String) {
        viewModelScope.launch {
            deleteItemFromCart.deleteItemFromCart(id).collectLatest {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    fetchNewCart()
                } else {
                    if (!it.message.isNullOrBlank()) {
                        _showToast.value = it.message!!
                    }
                }
            }
        }
    }

    fun fetchNewCart() {
        viewModelScope.launch {
            fetchCartUseCase.fetchCart().collectLatest {
                _currentCartLiveData.emit(it)
                if (it.status == RestClientResult.Status.SUCCESS) {
                    _isProceedBtnEnabled.emit(it.data?.data?.cartItemData?.isNullOrEmpty() == false)
                }
            }
        }
    }

    fun validatePinCode(toString: String) {
        viewModelScope.launch {
            validatePinCodeUseCase.validatePinCode(toString).collect {
                _validatePinCodeLiveData.emit(it)
            }
        }
    }

    fun addToWishList(
        addCartItemRequest: AddCartItemRequest,
        currentAvailableVolume: AvailableVolumeV2?,
        getString: (StringResource) -> String,
    ) {
        viewModelScope.launch {
            productWishlistUseCase.addProductToWishlist(addCartItemRequest).collect {
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    _showToast.value = getString(MR.strings.product_added_to_wishlist)
                    currentAvailableVolume?.wishListId = it.data?.data?.id
                    _isProductLiked.value = true
                } else if (it.status != RestClientResult.Status.LOADING) {
                    it.data?.errorMessage?.let {
                        _showToast.value = it
                    } ?: run {
                        _showToast.value = getString(MR.strings.something_went_wrong)
                    }
                }
            }
        }
    }

    fun removeFromWishList(addCartItemRequest: AvailableVolumeV2?, getString: (StringResource) -> String,) {
        val wishlistId = addCartItemRequest?.wishListId ?: ""
        viewModelScope.launch {
            removeProductFromWishlistUseCase.removeProductFromWishlist(wishlistId).collect {
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    _isProductLiked.value = false
                    _showToast.value = getString(MR.strings.product_removed_from_wishlist)
                    addCartItemRequest?.wishListId = null
                } else if (it.status != RestClientResult.Status.LOADING) {
                    it.data?.errorMessage?.let {
                        _showToast.value = it
                    } ?: run {
                        _showToast.value = getString(MR.strings.something_went_wrong)
                    }
                }
            }
        }
    }

    fun clearPincode() {

    }

    fun notifyUser(currentAvailableVolume: AvailableVolumeV2?, label: String?) {
        viewModelScope.launch {
            notifyUserUseCase.notifyUser(
                AddCartItemRequest(
                    currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                    currentAvailableVolume?.productId,
                    currentAvailableVolume?.volume,
                    label,
                )
            ).collect {
                _notifyLiveData.value = it
                if (it.status ==  RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    currentAvailableVolume?.isSetToNotify = true
                }
            }
        }
    }
    fun updateMoreInfoList(list: List<ExpandableDataItem>, position: Int) {
        viewModelScope.launch {
            val newList = list.toMutableList()
            newList.forEach { it.isExpanded = false }
            val item = newList[position]
            item.isExpanded = !item.isExpanded
            newList[position] = item
            _moreInfoLiveData.emit(newList)
        }
    }
    fun updateFAQList(list: List<ExpandableDataItem>, position: Int) {
        viewModelScope.launch {
            val newList = list.toMutableList()
            val item = newList[position]
            item.isExpanded = !item.isExpanded
            newList.forEach { it.isExpanded = false }
            newList[position] = item

            val cardList =
                newList.mapNotNull {it as? ExpandableDataItem.CardHeaderIsExpandedDataType? }

            _faqLiveData.emit(cardList)
        }
    }

    fun curateFaqItemFromBE(it: List<GenericFAQ>?): List<ExpandableDataItem> {
        val list = mutableListOf<ExpandableDataItem>()
        it?.forEach {
            if (!it.question.isNullOrBlank() && !it.answer.isNullOrBlank())
                list.add(
                    ExpandableDataItem.CardHeaderIsExpandedDataType(

                        question = it.question ?: "",
                        answer = it.answer ?: ""
                    ))
        }
        return list
    }
    fun fetchFaqs() {
        viewModelScope.launch {
            getStoreItemFaqUseCase.getStoreItemFaq().collect(
                onSuccess = {
                    if (it != null && it.genericFAQResponse?.genericFAQS?.isNotEmpty() == true) {
                        it.genericFAQResponse?.genericFAQS.let {
                            _faqLiveData.value = curateFaqItemFromBE(it)
                        }
                    }
                })
        }
    }

    fun setMoreInfoList(curateMoreInfoList: List<ExpandableDataItem>) {
        viewModelScope.launch {
            _moreInfoLiveData.emit(curateMoreInfoList)
        }
    }

    fun getProductFromLabel(label: String): ProductV2? {
        return _storeItemsLiveData.value?.data?.data?.products?.singleOrNull { it.label.equals(label) }
    }
    fun getProductFromLabel(productsV2: ProductsV2, label: String): ProductV2? {
        return productsV2.products?.singleOrNull { it.label.equals(label) }
    }
}