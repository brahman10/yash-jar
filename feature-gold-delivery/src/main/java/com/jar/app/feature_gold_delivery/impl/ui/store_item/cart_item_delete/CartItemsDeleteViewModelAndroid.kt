package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_item_delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartItemsDeleteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartItemsDeleteViewModelAndroid @Inject constructor(
    private val deleteItemFromCart: DeleteItemToCartUseCase,
) : ViewModel() {


    private val viewModel by lazy {
        CartItemsDeleteViewModel(
            deleteItemFromCart,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}