package com.jar.app.feature_gold_delivery.impl.ui.cart_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartItemsFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartItemsFragmentViewModelAndroid @Inject constructor(
    private val deleteAddressUseCase: DeleteItemToCartUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
) : ViewModel() {


    private val viewModel by lazy {
        CartItemsFragmentViewModel(
            deleteAddressUseCase,
            fetchCartUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}