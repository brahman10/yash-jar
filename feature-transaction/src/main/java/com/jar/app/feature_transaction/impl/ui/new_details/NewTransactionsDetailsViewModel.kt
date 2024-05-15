package com.jar.app.feature_transaction.impl.ui.new_details

import com.jar.app.feature_transaction.shared.domain.use_case.FetchNewTransactionDetailsUseCase
import androidx.lifecycle.*
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionContactUsCard
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionDetailsV5Data
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionDetailsCardView
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NewTransactionsDetailsViewModel @Inject constructor(
    private val fetchNewTransactionDetailsUseCase: FetchNewTransactionDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider
): ViewModel(){

    private val _cardsLiveData = MutableLiveData<List<NewTransactionDetailsCardView>>()
    val cardsLiveData: LiveData<List<NewTransactionDetailsCardView>>
        get() = _cardsLiveData

    private val _transactionDetailsLiveData = MutableLiveData<TransactionDetailsV5Data?>()
    val transactionDetailsLiveData: LiveData<TransactionDetailsV5Data?>
        get() = _transactionDetailsLiveData

    fun fetchTransactionDetails() {
        viewModelScope.launch(dispatcherProvider.io) {
            fetchNewTransactionDetailsUseCase.fetchNewTxnDetails(
                savedStateHandle.get<String>("orderId")!!,
                savedStateHandle.get<String>("sourceType")!!,
                savedStateHandle.get<String>("txnId")!!
            ).collectUnwrapped(onSuccess = { restResult ->

                val result = restResult.data
                _transactionDetailsLiveData.postValue(result)

                val list = ArrayList<NewTransactionDetailsCardView>()
                result?.let {
                    result.transactionHeaderCard?.let {
                        list.add(it)
                    }

                    result.transactionOrderDetailsComponent?.let {
                        list.add(it)
                    }

                    result.transactionStatusCard?.let {
                        list.add(it)
                    }

                    list.add(NewTransactionContactUsCard())
                    list.sortBy { it.getSortKey() }
                }
                _cardsLiveData.postValue(list)
            })
        }
    }

}