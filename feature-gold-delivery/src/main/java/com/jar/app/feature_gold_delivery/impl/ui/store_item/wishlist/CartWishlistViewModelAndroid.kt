package com.jar.app.feature_gold_delivery.impl.ui.store_item.wishlist

import androidx.lifecycle.ViewModel
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithoutFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetProductsFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartWishListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope


@HiltViewModel
class CartWishListViewModelAndroid @Inject constructor(
    private val fetchWishlistUseCase: GetProductsFromWishlistUseCase,
    private val productWishlistUseCase: RemoveProductFromWishlistUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val addItemToCartUseCase: AddItemToCartWithoutFlowUseCase,
    private val fetchTransactionListingUseCase: GetProductsFromWishlistUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        CartWishListViewModel(
            fetchWishlistUseCase,
            productWishlistUseCase,
            getAllStoreItemsUseCase,
            addItemToCartUseCase,
            fetchTransactionListingUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}