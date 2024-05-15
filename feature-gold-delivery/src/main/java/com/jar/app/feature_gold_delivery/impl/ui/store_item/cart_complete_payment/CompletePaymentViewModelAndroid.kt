package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_complete_payment

import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderStatusUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.AddDeliveryAddressFragmentViewModel
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CompletePaymentFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompletePaymentFragmentViewModelAndroid @Inject constructor(
    private val goldDeliveryPaymentUseCase: FetchOrderStatusUseCase,
    private val goldDeliveryPostOrder: PostOrderUseCase,

    ) : ViewModel() {


    private val viewModel by lazy {
        CompletePaymentFragmentViewModel(
            goldDeliveryPaymentUseCase,
            goldDeliveryPostOrder,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}