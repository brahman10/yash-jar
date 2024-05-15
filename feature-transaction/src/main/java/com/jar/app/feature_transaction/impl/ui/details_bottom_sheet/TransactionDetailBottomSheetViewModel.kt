package com.jar.app.feature_transaction.impl.ui.details_bottom_sheet

import androidx.lifecycle.*
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPostSetupTransactionDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView
import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TransactionDetailBottomSheetViewModel @Inject constructor(
    private val fetchPostSetupTransactionDetailsUseCase: FetchPostSetupTransactionDetailsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _cardsLiveData = MutableLiveData<List<TxnDetailsCardView>>()
    val cardsLiveData: LiveData<List<TxnDetailsCardView>>
        get() = _cardsLiveData

    var commonData: com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData? = null
    var weeklyMagicShown = false

    fun fetchIsFailedTransaction(): Boolean {
        return commonData?.status?.let {
            it.uppercase() == CommonTransactionStatus.FAILED.name
        } ?: run {
            false
        }

    }

    fun fetchTransactionDetails(id: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            fetchPostSetupTransactionDetailsUseCase.fetchPostSetupTransactionDetails(id)
                .collectUnwrapped(onSuccess = { restResult ->
                    val result = restResult.data
                    val list = ArrayList<TxnDetailsCardView>()

                    result.headers?.let {
                        commonData =
                            com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData(
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
                            com.jar.app.feature_transaction.shared.domain.model.WinningsUsedData(
                                it
                            )
                        )
                    }

                    if ((result.roundoffCount ?: 0) > 0)
                        list.add(
                            com.jar.app.feature_transaction.shared.domain.model.RoundOffData(
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
                            com.jar.app.feature_transaction.shared.domain.model.WeeklyChallengeData(
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

                    result.savingTxnDetails?.let {
                        list.add(it)
                    }

                    result.pauseTxnDetails?.let {
                        list.add(it)
                    }

                    if (result.txnRoutine.isNullOrEmpty().not())
                        list.add(result.getTxnRoutineDetails())

                    result.txnDetailsBottomObjects?.let {
                        if (it.isNotEmpty())
                            list.add(
                                com.jar.app.feature_transaction.shared.domain.model.TxnDetailsData(
                                    it.first().title,
                                    it.first().value,
                                    it.subList(1, it.size)
                                )
                            )
                    }
                    result.shouldShowContactUs?.let {
                        if (it)
                            list.add(com.jar.app.feature_transaction.shared.domain.model.ContactUsData())
                    }
                    list.sortBy { it.getSortKey() }
                    delay(500)  //Added intentionally
                    _cardsLiveData.postValue(list)
                })
        }
    }
}