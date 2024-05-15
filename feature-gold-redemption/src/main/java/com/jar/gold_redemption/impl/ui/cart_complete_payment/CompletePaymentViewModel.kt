package com.jar.gold_redemption.impl.ui.cart_complete_payment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.core_compose_ui.views.payments.TimelineViewData
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiateOrderUseCase
import com.jar.app.feature_gold_redemption.shared.util.curateLoadingStatus
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromPaymentStatus
import com.jar.app.feature_gold_redemption.shared.util.getGoldRedemptionStatusFromString
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class CompletePaymentFragmentViewModel @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    private val voucherOrderPlacementUseCase: VoucherInitiateOrderUseCase
) : ViewModel() {
    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _title = MutableLiveData<String>("")
    val title: LiveData<String> = _title

    private val _bonusGoldText = MutableLiveData<String>("")
    val bonusGoldText: LiveData<String> = _bonusGoldText

    private val _finalStatus = MutableLiveData<GoldRedemptionManualPaymentStatus>()
    val finalStatus: LiveData<GoldRedemptionManualPaymentStatus> = _finalStatus

    private val _voucherCardList = MutableLiveData<List<UserVoucher>>()
    val voucherCardList: LiveData<List<UserVoucher>> = _voucherCardList

    private val _refundList = MutableLiveData<List<LabelAndValueCompose>>()
    val refundList: LiveData<List<LabelAndValueCompose>> = _refundList

    private val _showMyOrdersButton = MutableLiveData<Boolean>(false)
    val showMyOrdersButton: LiveData<Boolean> = _showMyOrdersButton

    private val _showContinueShoppingButton = MutableLiveData<ContinueShoppingButtonPlacement>(ContinueShoppingButtonPlacement.NONE)
    val showContinueShoppingButton: LiveData<ContinueShoppingButtonPlacement> = _showContinueShoppingButton

    private val _voucherDetailsList = MutableLiveData<List<LabelAndValueCompose>>()
    val voucherDetailsList: LiveData<List<LabelAndValueCompose>> = _voucherDetailsList

    private val _paymentDetailsList = MutableLiveData<List<LabelAndValueCompose>>()
    val paymentDetailsList: LiveData<List<LabelAndValueCompose>> = _paymentDetailsList

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }
    private val _orderStatusLiveData =
        MutableLiveData<List<TimelineViewData>>()
    val orderStatusLiveData: LiveData<List<TimelineViewData>>
        get() = _orderStatusLiveData

    private val _statusBottomText =
        MutableLiveData<String>()
    val statusBottomText: LiveData<String>
        get() = _statusBottomText

    private val _placeOrderAPILiveData =
        SingleLiveEvent<InitiatePaymentResponse>()
    val placeOrderAPILiveData: LiveData<InitiatePaymentResponse>
        get() = _placeOrderAPILiveData

    fun fetchOrderStatus(
        orderId: String,
        paymentProvider: String,
        weakReference: WeakReference<Context?>,
        times: Int = 1,
        showLoadingFxn: () -> Unit = {},
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean = { false }
    ) {
        viewModelScope.launch {
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                FetchManualPaymentRequest(
                    orderId = orderId,
                    paymentProvider = paymentProvider // For gold redemption flow, BE decides whether paytm or juspay
                ),
                times,
                showLoadingFxn,
                shouldRetry
            ).collectUnwrapped(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    it.data?.let { data ->
                        processStatusResponse(data, weakReference = weakReference)
                        if (data.createVoucherOrderResponse == null) {
                            showLoading(true)
                        } else {
                            showLoading(false)
                        }
                    }
                },
                onError = { message, errorCode ->
                    showLoading(false)
                }
            )
        }
    }

    /*
    * Processes the response from BE for computing the status of order
    * Returns if the state of payment isn't completed from BE
    * */
    fun processStatusResponse(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse, weakReference: WeakReference<Context?>): Boolean {
        val data = fetchManualPaymentStatusResponse?.createVoucherOrderResponse
        val curateLoadingStatus = curateLoadingStatus(fetchManualPaymentStatusResponse)
        _voucherDetailsList.value = curateVoucherDetailsList(data, weakReference)
        _paymentDetailsList.value = curatePaymentDetailsList(data?.paymentOrderDetails, weakReference)
        _title.value = curateTitle(curateLoadingStatus, weakReference)
        _bonusGoldText.value = data?.bonusGoldText.orEmpty() // curateTitle(data)
        if (_finalStatus.value != curateLoadingStatus)
            _finalStatus.value = curateLoadingStatus
        _voucherCardList.value = curateVoucherCardList(data?.voucherList, curateLoadingStatus, data?.voucherOrderDetails?.productType)
        _showMyOrdersButton.value = shouldShowMyOrdersButton(curateLoadingStatus, getGoldRedemptionStatusFromPaymentStatus(fetchManualPaymentStatusResponse.getManualPaymentStatus()), getGoldRedemptionStatusFromString(fetchManualPaymentStatusResponse?.createVoucherOrderResponse?.voucherOrderStatus))
        _showContinueShoppingButton.value = shouldContinueShoppingButton(curateLoadingStatus, getGoldRedemptionStatusFromPaymentStatus(fetchManualPaymentStatusResponse.getManualPaymentStatus()), getGoldRedemptionStatusFromString(fetchManualPaymentStatusResponse?.createVoucherOrderResponse?.voucherOrderStatus))

        if (curateLoadingStatus in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED)) {
            _orderStatusLiveData.postValue(emptyList())
            _statusBottomText.value = ""
            return true
        } else {
            val list = curateStatus(fetchManualPaymentStatusResponse, weakReference)
            _statusBottomText.value = curateBottomText(
                fetchManualPaymentStatusResponse,
                weakReference,
            )
            _orderStatusLiveData.postValue(list)
        }
        return false
    }

    private fun curateBottomText(
        fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse,
        weakReference: WeakReference<Context?>,
    ): String {
        val paymentStatus = getGoldRedemptionStatusFromPaymentStatus(fetchManualPaymentStatusResponse.getManualPaymentStatus())
        val orderStatus = getGoldRedemptionStatusFromString(fetchManualPaymentStatusResponse.createVoucherOrderResponse?.voucherOrderStatus)

        return if (paymentStatus in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED) &&
                    orderStatus in setOf(GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.PROCESSING)) {
             weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_processing_voucher_purchase).orEmpty()
        } else if (paymentStatus in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED) &&
                    orderStatus in setOf(GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE)) {
          weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_amount_refunded).orEmpty()
        } else {
            ""
        }
    }

    private fun curateTitle(curateLoadingStatus: GoldRedemptionManualPaymentStatus, weakReference: WeakReference<Context?>): String {
        return when (curateLoadingStatus) {
            GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED -> weakReference?.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_congratulations_your_voucher_is_here).orEmpty()
            GoldRedemptionManualPaymentStatus.INITIALIZE, GoldRedemptionManualPaymentStatus.PENDING, GoldRedemptionManualPaymentStatus.PROCESSING, null -> weakReference?.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_your_order_is_being_processed).orEmpty()
            GoldRedemptionManualPaymentStatus.FAILURE, GoldRedemptionManualPaymentStatus.FAILED -> weakReference?.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_we_unable_to_process).orEmpty()
            GoldRedemptionManualPaymentStatus.REFUNDED, GoldRedemptionManualPaymentStatus.REFUND_INITIATED, GoldRedemptionManualPaymentStatus.REFUND_PENDING, GoldRedemptionManualPaymentStatus.REFUND_PROCESSING, GoldRedemptionManualPaymentStatus.REFUND_FAILED -> ""
        }
    }

    fun retryPaymentFlow(initOrderRequest: GoldRedemptionInitiateCreateOrderRequest) {
        viewModelScope.launch {
            voucherOrderPlacementUseCase.initiateOrder(initOrderRequest).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    _placeOrderAPILiveData.value = it
                },
                onError = { _, _ ->
                    showLoading(false)
                }
            )
        }
    }
    fun startPolling(orderId: String, name: String, context: WeakReference<Context?>) {
        fetchOrderStatus(
            orderId,
            name,
            context,
            times = Int.MAX_VALUE,
            showLoadingFxn = {
                 _showLoading.value = true
            },
            shouldRetry = {
                val data = it.data?.data
                if (data?.createVoucherOrderResponse == null || it.status == RestClientResult.Status.LOADING) {
                    showLoading(true)
                } else {
                    showLoading(false)
                }
                if (it.status == RestClientResult.Status.SUCCESS && it.data?.success == true) {
                    val data = it.data?.data
                    val curateLoadingStatus = curateLoadingStatus(data)
                    curateLoadingStatus !in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED, GoldRedemptionManualPaymentStatus.FAILED, GoldRedemptionManualPaymentStatus.FAILURE)
                } else {
                    true
                }
            }
        )
    }
}