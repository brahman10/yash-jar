package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_item_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartItemsAddViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartItemsAddViewModelAndroid @Inject constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        CartItemsAddViewModel(
            updateCartUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}