package com.jar.app.feature_transaction.impl.ui.retry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.app.feature_transaction.shared.domain.use_case.PostTransactionActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RetryGoldWithdrawalViewModel @Inject constructor(
    private val postTransactionActionUseCase: PostTransactionActionUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _withdrawAcceptanceLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse>>>()
    val withdrawAcceptanceLiveData: MutableLiveData<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse>>>
        get() = _withdrawAcceptanceLiveData

    fun postRetryWithdrawRequest(orderId: String, vpa: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            postTransactionActionUseCase.postTransactionAction(
                type = TransactionActionType.RETRY_PAYOUT_VPA,
                orderId = orderId,
                vpa = vpa
            ).collect {
                _withdrawAcceptanceLiveData.postValue(it)
            }
        }
    }
}