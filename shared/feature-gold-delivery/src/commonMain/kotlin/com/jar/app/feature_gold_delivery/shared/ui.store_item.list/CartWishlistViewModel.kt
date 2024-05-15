package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_delivery.shared.data.WishlistData
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_gold_delivery.shared.domain.model.WishlistAPIData
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithoutFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetProductsFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.kuuurt.paging.multiplatform.insertPagingSeparators
import com.kuuurt.paging.multiplatform.map
import kotlinx.coroutines.flow.StateFlow


class CartWishListViewModel constructor(
    private val fetchWishlistUseCase: GetProductsFromWishlistUseCase,
    private val productWishlistUseCase: RemoveProductFromWishlistUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val addItemToCartUseCase: AddItemToCartWithoutFlowUseCase,
    private val fetchTransactionListingUseCase: GetProductsFromWishlistUseCase,
    coroutineScope: CoroutineScope?
) {
    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonStateFlow()

    private val _cartItemsAdded =
        MutableSharedFlow<Boolean>()
    val cartItemsAdded: CFlow<Boolean>
        get() = _cartItemsAdded.toCommonFlow()

    private val _deleteAddressLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val deleteAddressLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonFlow()

    var if24HourAdded = false
    var lastMonthAdded = false
    val pager: Pager<Int, WishlistData> = Pager(
        viewModelScope,
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = NETWORK_PAGE_SIZE
        ),
        initialKey = 0,
        getItems = { currentKey, size ->
            val response = fetchTransactionListingUseCase.getProductsFromWishlist(
                currentKey, size,
            )
            val items = response.data?.data?.wishList.orEmpty().map {
                WishlistData.WishlistBody(
                    it, false, CartItemData(
                        amount = it.amount?.toDouble(),
                        quantity = 1,
                        label = it.label,
                        productId = it.productId.toString(),
                        volume = it.volume.orZero().toDouble(),
                        inStock = true,
                        deliveryMakingCharge = null,
                        id = it.id,
                        icon = null,
                        discountOnTotal = null,
                        totalAmount = null
                    )
                ) as WishlistData
            }
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { currentKey - 1 },
                nextKey = { currentKey + 1 }
            )
        },
    )

    val pagingData = pager.pagingData
        .map {
            it.insertPagingSeparators { before, after ->
                if (after == null && before is WishlistData.WishlistBody && isAddedByYou(before?.body)) {
                    // first item
                    if (!if24HourAdded) {
                        if24HourAdded = true
                        WishlistData.WishlistHeader(
                            "ADDED BY YOU"
                        )
                    } else {
                        return@insertPagingSeparators null
                    }
                } else if (before is WishlistData.WishlistBody && isAddedByYou(before?.body)) {
                    // do nothing as already added
                    return@insertPagingSeparators null
                } else if (((after == null && before is WishlistData.WishlistBody && !isAddedByYou(
                        before?.body
                    )) || !lastMonthAdded) && before != null
                ) {
                    lastMonthAdded = true
                    WishlistData.WishlistHeader(
                        "SET TO NOTIFY"
                    )
                } else {
                    // do nothing let it add
                    return@insertPagingSeparators null
                }
            }
        }
        .cachedIn(viewModelScope).toCommonFlow()

    fun resetSeperators() {
        if24HourAdded = false
        lastMonthAdded = false
    }

    fun fetchProducts() {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(null).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }

    private fun isAddedByYou(body: WishlistAPIData?): Boolean {
        return body?.setToNotify == false
    }
    fun deleteCartItem(it1: String) {
        viewModelScope.launch {
            productWishlistUseCase.removeProductFromWishlist(it1).collect {
                _deleteAddressLiveData.emit(it)
            }
        }
    }

    fun addItemsToCart(snapshot: List<WishlistData.WishlistBody?>) {
        viewModelScope.launch {
                val runningTasks = snapshot.map { id ->
                    async { // this will allow us to run multiple tasks in parallel
                        val apiResponse = addItemToCartUseCase.addItemToCartWithoutFlow(
                            AddCartItemRequest(
                                deliveryMakingCharge = null,
                                volume = id?.body?.volume?.toDouble(),
                                productId = id?.body?.productId?.toInt(),
                                quantity = 1,
                                label = id?.body?.label
                            )
                        )
                        id to apiResponse // associate id and response for later
                    }
                }

                val responses = runningTasks.awaitAll()

                val all = responses.all { (id, response) ->
                    response.status == RestClientResult.Status.SUCCESS
                }
                _cartItemsAdded.emit(all)
            }
        }
}