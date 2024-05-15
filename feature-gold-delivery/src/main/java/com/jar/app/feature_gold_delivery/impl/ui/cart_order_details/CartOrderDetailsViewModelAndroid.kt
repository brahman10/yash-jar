package com.jar.app.feature_gold_delivery.impl.ui.cart_order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderDetailUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartOrderDetailFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CartOrderDetailFragmentViewModelAndroid @Inject constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val fetchCartUseCase: FetchOrderDetailUseCase,

    ) : ViewModel() {

    private val viewModel by lazy {
        CartOrderDetailFragmentViewModel(
            updateCartUseCase,
            getAllStoreItemsUseCase,
            fetchCartUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}