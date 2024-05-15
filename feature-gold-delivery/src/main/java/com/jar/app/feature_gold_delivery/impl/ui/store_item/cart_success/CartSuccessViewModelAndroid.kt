package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_success

import com.jar.app.feature_gold_delivery.shared.domain.use_case.CartOrderUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.AddDeliveryAddressFragmentViewModel
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartSuccessFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartSuccessFragmentViewModelAndroid @Inject constructor(
    private val cartOrderUseCase: CartOrderUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,

    ) : ViewModel() {


    private val viewModel by lazy {
        CartSuccessFragmentViewModel(
            cartOrderUseCase,
            fetchOrderStatusDynamicCardsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}