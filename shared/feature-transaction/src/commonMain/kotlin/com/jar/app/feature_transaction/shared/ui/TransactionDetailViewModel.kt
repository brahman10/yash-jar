package com.jar.app.feature_transaction.shared.ui

import com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData
import com.jar.app.feature_transaction.shared.domain.model.ContactUsData
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.app.feature_transaction.shared.domain.model.RoundOffData
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData
import com.jar.app.feature_transaction.shared.domain.model.WeeklyChallengeData
import com.jar.app.feature_transaction.shared.domain.model.WinningsUsedData
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTxnDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TransactionDetailViewModel constructor(
    private val fetchTxnDetailsUseCase: IFetchTxnDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _cardsLiveData = MutableSharedFlow<List<TxnDetailsCardView>>()
    val cardsLiveData: CFlow<List<TxnDetailsCardView>>
        get() = _cardsLiveData.toCommonFlow()

    private val _transactionDetailFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<NewTransactionDetails>>>(
            RestClientResult.none()
        )
    val transactionDetailFlow: CStateFlow<RestClientResult<ApiResponseWrapper<NewTransactionDetails>>>
        get() = _transactionDetailFlow.toCommonStateFlow()

    var commonData: CommonTxnWinningData? = null
    var weeklyMagicShown = false

    private var job: Job? = null

    fun fetchTransactionDetails(
        orderId: String,
        sourceType: String,
        txnId: String
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            fetchTxnDetailsUseCase.fetchTxnDetails(
                orderId,
                sourceType,
                txnId
            ).collectUnwrapped(
                onLoading = {
                    _transactionDetailFlow.emit(RestClientResult.loading())
                },
                onSuccess = { restResult ->
                    _transactionDetailFlow.emit(RestClientResult.success(restResult))
                    val result = restResult.data
                    val list = ArrayList<TxnDetailsCardView>()

                    result.headers?.let {
                        commonData =
                            CommonTxnWinningData(
                                title = it.title,
                                subtitle = it.subTitle,
                                iconLink = it.iconLink,
                                amount = it.amount,
                                date = it.date,
                                status = it.currentStatus,
                                statusInfo = it.statusInfo,
                                sourceType = it.sourceType,
                                txnId = it.assetTransactionId,
                                orderId = it.orderId,
                                valueType = it.valueType,
                                volume = it.volume
                            )
                        list.add(commonData!!)
                    }

                    result.jarWinningsUsedText?.takeIf { it.isNotBlank() }?.let {
                        list.add(
                            WinningsUsedData(
                                it
                            )
                        )
                    }

                    if ((result.roundoffCount ?: 0) > 0)
                        list.add(
                            RoundOffData(
                                commonData?.orderId,
                                null,
                                result.roundoffCount
                            )
                        )

                    result.productDetails?.let {
                        list.add(it)
                    }

                    result.couponCodeDetails?.let {
                        list.add(it)
                    }

                    result.challengeId?.let {
                        list.add(
                            WeeklyChallengeData(
                                it
                            )
                        )
                        weeklyMagicShown = true
                    }

                    result.giftingDetails?.let {
                        list.add(it)
                    }

                    result.trackingInfo?.let {
                        list.add(it)
                    }

                    result.leasingTnxDetails?.let {
                        list.add(it)
                    }

                    list.add(result.getTxnRoutineDetails())

                    result.txnDetailsBottomObjects?.let {
                        if (it.isNotEmpty())
                            list.add(
                                TxnDetailsData(
                                    it.first().title,
                                    it.first().value,
                                    it.subList(1, it.size)
                                )
                            )
                    }
                    list.add(ContactUsData())
                    list.sortBy { it.getSortKey() }
                    delay(500)  //Added intentionally
                    _cardsLiveData.emit(list)
                },
                onError = { errorMessage, errorCode ->
                    _transactionDetailFlow.emit(
                        RestClientResult.error(
                            message = errorMessage,
                            errorCode = errorCode
                        )
                    )
                }
            )
        }
    }
}