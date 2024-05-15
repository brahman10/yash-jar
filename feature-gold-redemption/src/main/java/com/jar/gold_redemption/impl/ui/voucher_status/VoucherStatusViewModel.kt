package com.jar.gold_redemption.impl.ui.voucher_status


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_BONUS
import com.jar.app.core_compose_ui.views.payments.TimelineViewData
import com.jar.app.core_compose_ui.views.payments.TransactionStatus
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherTxnDetailsUseCase
import com.jar.gold_redemption.impl.ui.cart_complete_payment.curatePaymentDetailsList
import com.jar.gold_redemption.impl.ui.cart_complete_payment.curateStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import com.jar.app.feature_gold_redemption.shared.data.network.model.RefundDetails
import com.jar.app.feature_gold_redemption.shared.domain.model.RefundStatus
import com.jar.gold_redemption.impl.ui.voucher_purchase.buildLabelComposeForRefundDetails
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.app.feature_gold_redemption.shared.util.curateLoadingStatus
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromString
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class VoucherStatusViewModel @Inject constructor(
    private val voucherOrderPlaceUseCase: VoucherTxnDetailsUseCase
) : ViewModel() {

    var voucherType: String? = null
    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading
    var curateLoadingStatus: GoldRedemptionManualPaymentStatus? = null

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }

    private val _transactionDetails = MutableLiveData<GoldRedemptionTransactionData>()
    val transactionDetails: LiveData<GoldRedemptionTransactionData> = _transactionDetails

    private val _addedRow = MutableLiveData<PurchaseItemData?>()
    val addedRow: LiveData<PurchaseItemData?> = _addedRow

    private val _timelineViewList = MutableLiveData<List<TimelineViewData>?>()
    val timelineViewList: LiveData<List<TimelineViewData>?> = _timelineViewList

    private val _orderDetails = MutableLiveData<List<LabelAndValueCompose>>()
    val orderDetails: LiveData<List<LabelAndValueCompose>> = _orderDetails

    private val _refundDetails = MutableLiveData<List<LabelAndValueCompose>>()
    val refundDetails: LiveData<List<LabelAndValueCompose>> = _refundDetails

    private val _showOrderId = MutableLiveData<Boolean>(true)
    val showOrderId: LiveData<Boolean> = _showOrderId

    private val _showToast = MutableLiveData<String>("")
    val showToast: LiveData<String> = _showToast

    private val _statusBottomText = MutableLiveData<String>("")
    val statusBottomText: LiveData<String> = _statusBottomText

    private val _headingText = MutableLiveData<String>("")
    val headingText: LiveData<String> = _headingText

    private val _finalStatus = MutableLiveData<GoldRedemptionManualPaymentStatus?>(null)
    val finalStatus: LiveData<GoldRedemptionManualPaymentStatus?> = _finalStatus

    fun processRefundResponse(
        data: GoldRedemptionTransactionData,
        weakReference: WeakReference<Context?>,
        refundDetails: RefundDetails
    ): GoldRedemptionManualPaymentStatus {
        val list = mutableListOf<TimelineViewData>()
        val context = weakReference.get()
        list.add(
            TimelineViewData(
                TransactionStatus.SUCCESS,
                context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_initiated).orEmpty(),
                refundDetails.refundedOn
            )
        )

        if (refundDetails.getRefundStatus() in setOf(
                RefundStatus.REFUND_INITIATED,
                RefundStatus.REFUND_PENDING,
                RefundStatus.REFUND_PROCESSING,
            ) || refundDetails.transactionId == null
        ) {
            list.add(
                TimelineViewData(
                    TransactionStatus.PENDING,
                    context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_complete).orEmpty(),
                    null
                )
            )
        } else {
            list.add(
                TimelineViewData(
                    TransactionStatus.SUCCESS,
                    context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_complete).orEmpty(),
                    null
                )
            )
        }

        _timelineViewList.postValue(list)
        _statusBottomText.value =
            context?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_amount_refunded)
        return if (refundDetails.transactionId == null) GoldRedemptionManualPaymentStatus.PROCESSING else GoldRedemptionManualPaymentStatus.SUCCESS
    }

    fun processStatusResponse(
        data: GoldRedemptionTransactionData,
        weakReference: WeakReference<Context?>,
        isForBonus: Boolean,
    ): GoldRedemptionManualPaymentStatus {
        curateLoadingStatus = curateLoadingStatus(
            data.getManualPaymentStatus(), data.getManualOrderStatus(isForBonus)
        )
        val paymentTitle =
            if (isForBonus && curateLoadingStatus == GoldRedemptionManualPaymentStatus.SUCCESS) "Bonus initiated" else null
        val orderTitle =
            if (isForBonus && curateLoadingStatus == GoldRedemptionManualPaymentStatus.SUCCESS) "Gold added to locker" else null
        val list = curateStatus(
            data,
            weakReference,
            paymentTitle,
            orderTitle,
            isForBonus
        )
        _timelineViewList.postValue(list)
        _statusBottomText.value = curateBottomText(data, isForBonus)
        return curateLoadingStatus as GoldRedemptionManualPaymentStatus
    }

    private fun curateBottomText(
        fetchManualPaymentStatusResponse: GoldRedemptionTransactionData,
        isForBonus: Boolean
    ): String {
        if (isForBonus) return ""
        val paymentStatus = getGoldRedemptionStatusFromString(fetchManualPaymentStatusResponse.txnStatus)
        val orderStatus =
            getGoldRedemptionStatusFromString(fetchManualPaymentStatusResponse.voucherOrderStatus)

        return if (paymentStatus in setOf(
                GoldRedemptionManualPaymentStatus.SUCCESS,
                GoldRedemptionManualPaymentStatus.COMPLETED
            ) &&
            orderStatus in setOf(
                GoldRedemptionManualPaymentStatus.PENDING,
                GoldRedemptionManualPaymentStatus.PROCESSING
            )
        ) {
            "We're processing your voucher purchase and will update you within 30 minutes."
        } else if (paymentStatus in setOf(
                GoldRedemptionManualPaymentStatus.SUCCESS,
                GoldRedemptionManualPaymentStatus.COMPLETED
            ) &&
            orderStatus in setOf(
                GoldRedemptionManualPaymentStatus.FAILED,
                GoldRedemptionManualPaymentStatus.FAILURE
            )
        ) {
            "The amount will be refunded to your source account within 48 hours."
        } else {
            ""
        }
    }

    fun processVoucherStatusResponse(
        it: GoldRedemptionTransactionData,
        weakReference: WeakReference<Context?>
    ) {

        var status: GoldRedemptionManualPaymentStatus? = null
        val isForBonus = voucherType == VOUCHER_BONUS

        showLoading(false)
        _transactionDetails.value = it
        if (it.refundDetails != null) {
            val refundDetails: RefundDetails = it.refundDetails as RefundDetails
            status = processRefundResponse(it, weakReference, refundDetails)
            _refundDetails.value = buildLabelComposeForRefundDetails(
                refundDetails,
                weakReference.get()
            )
        } else {
            _orderDetails.value =
                curatePaymentDetailsList(it?.paymentOrderDetails, weakReference)
            status =
                it?.let { it1 -> processStatusResponse(it1, weakReference, isForBonus) }
        }
        _showOrderId.value = isForBonus
        _headingText.value =
            generateHeading(status, isForBonus, weakReference, it?.refundDetails)
        if (voucherType == VOUCHER_BONUS) {
            _addedRow.value = PurchaseItemData(
                amount = it.amount,
                amountQuantityString = it.quantityMultipliedAmountString
                    ?: it.bonusQuantityMultipliedAmountString,
                date = it.dateString,
                dateString = it.dateString,
                desc = it.brandName,
                imageUrl = it.imageUrl,
                txnStatus = it.goldBonusTransactionStatus,
                title = it.voucherTypeString,
                bottomDrawerObjectType = null,
                quantity = null,
                voucherId = null,
                goldBonusTxnStatus = null,
                voucherOrderStatus = it.voucherOrderStatus,
                refundStatus = null
            )
        }
        if (_finalStatus.value != status) _finalStatus.value = status
    }

    fun fetchTransactionDetails(
        orderId: String?,
        enum: String?,
        weakReference: WeakReference<Context?>,
        shouldRetry: (result: RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>) -> Boolean
    ) {
        viewModelScope.launch {
            voucherOrderPlaceUseCase.fetchTxnDetails(orderId, enum, {
                showLoading(true)
            }, shouldRetry).collect(
                onLoading = {
                    showLoading(true)
                },
                onError = { message, _ ->
                    showLoading(false)
                    _showToast.value = message
                },
                onSuccess = {
                    it?.let {
                        processVoucherStatusResponse(it, weakReference)
                    }
                }
            )
        }
    }

    private fun generateHeading(
        status: GoldRedemptionManualPaymentStatus?,
        forBonus: Boolean,
        weakReference: WeakReference<Context?>,
        refundDetails: RefundDetails?
    ): String? {
        if (refundDetails != null) {
            return when (refundDetails.getRefundStatus()) {
                RefundStatus.REFUNDED -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_completed).orEmpty()

                RefundStatus.REFUND_PENDING, null,
                RefundStatus.REFUND_PROCESSING,
                RefundStatus.REFUND_FAILED, RefundStatus.REFUND_INITIATED -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_initiated).orEmpty()
            }
        }
        return if (forBonus)
            when (status) {
                GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_bonus_successful)
                    ?: "Bonus Successful"

                GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.INITIALIZE -> "Bonus pending"
                GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> "null"
                null -> "null"
                GoldRedemptionManualPaymentStatus.PROCESSING -> "null"
                GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> ""
            } else {
            when (status) {
                GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_purchase_successful)
                    ?: "Purchase Successful"

                GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PROCESSING -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_purchase_pending)
                    ?: "Purchase Pending"

                GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_purchase_failed)
                    ?: "Purchase Failed"

                GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> "Successful"

                null -> weakReference.get()
                    ?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_purchase_failed)
                    ?: "Voucher Purchase Failed"
            }
        }
    }

    fun startPolling(
        orderId: String?,
        enumType: String?,
        weakReference1: WeakReference<Context?>
    ) {
        fetchTransactionDetails(orderId, enumType, weakReference1) {
            var pollingContinue = false
            _showLoading.value = it.status != RestClientResult.Status.LOADING
            val data = it.data?.data ?: return@fetchTransactionDetails false

            data?.refundDetails?.let {
                pollingContinue = it.getRefundStatus() != RefundStatus.REFUNDED
            } ?: run {
                val isForBonus = data.goldBonusTransactionStatus != null
                val curateLoadingStatus = curateLoadingStatus(
                    data.getManualPaymentStatus() ?: GoldRedemptionManualPaymentStatus.FAILURE,
                    data.getManualOrderStatus(isForBonus)
                )
                pollingContinue = curateLoadingStatus in setOf(
                    GoldRedemptionManualPaymentStatus.PENDING,
                    GoldRedemptionManualPaymentStatus.PROCESSING
                )
            }
            processVoucherStatusResponse(data, weakReference1)
            pollingContinue
        }
    }
}
