package com.jar.app.feature_gold_delivery.impl.ui.store_item.my_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchTransactionListingUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartMyOrdersFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartMyOrdersFragmentViewModelAndroid @Inject constructor(
    private val transactionListingUseCase: FetchTransactionListingUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        CartMyOrdersFragmentViewModel(
            transactionListingUseCase,
            getAllStoreItemsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}