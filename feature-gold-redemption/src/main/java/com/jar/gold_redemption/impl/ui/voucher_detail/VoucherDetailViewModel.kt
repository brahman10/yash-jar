package com.jar.gold_redemption.impl.ui.voucher_detail


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.app.feature_gold_redemption.shared.data.network.model.RefundDetails
import com.jar.gold_redemption.impl.ui.voucher_purchase.curateRefundDetails
import com.jar.gold_redemption.impl.ui.voucher_purchase.curateVoucherPurchaseFaqs
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherViewDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class VoucherDetailViewModel @Inject constructor(
    private val voucherDiscoveryUseCase: VoucherViewDetailsUseCase
) : ViewModel() {

    private val _faqListLiveData = SingleLiveEvent<List<ExpandableCardModel>>()
    val faqListLiveData: LiveData<List<ExpandableCardModel>>
        get() = _faqListLiveData

    private val _brandName = MutableLiveData<String>("")
    val brandName: LiveData<String> = _brandName

    private val _voucherData = MutableLiveData<ViewVoucherDetailsAPIResponse?>()
    val voucherData: LiveData<ViewVoucherDetailsAPIResponse?> = _voucherData

    private val _refundDetails = MutableLiveData<RefundDetails?>()
    val refundDetails: LiveData<RefundDetails?> = _refundDetails

    private val _userVoucher = MutableLiveData<UserVoucher?>()
    val userVoucher: LiveData<UserVoucher?> = _userVoucher

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _showToast = MutableLiveData<String>("")
    val showToast: LiveData<String> = _showToast

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }

    fun fetchVoucherDetail(
        voucherId: String,
        orderId: String,
        weakReference: WeakReference<Context?>
    ) {
        viewModelScope.launch {
            voucherDiscoveryUseCase.fetchViewDetails(voucherId, orderId).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    _voucherData.value = it
                    _refundDetails.value = it?.refundDetails
                    _userVoucher.value = it?.convertToUserVoucher()

                    _faqListLiveData.value = curateVoucherPurchaseFaqs(
                        if (it?.getVoucherStatusEnum() !in setOf(CardStatus.FAILED)) it?.howToRedeemList else null,
                        if (it?.getVoucherStatusEnum() !in setOf(CardStatus.FAILED)) it?.tncList else null,
                        if (it?.refundDetails == null) it?.paymentOrderDetails else null,
                        true,
                        it?.amount,
                        weakReference = weakReference
                    ).toMutableList().apply {
                        it?.refundDetails?.let {
                            for (x in curateRefundDetails(it, weakReference)) {
                                add(x)
                            }
                        }
                    }
                    it?.brandTitle?.let {
                        _brandName.value = it
                    }
                },
                onError = { message, _ ->
                    showLoading(false)
                    _showToast.value = message
                }
            )
        }
    }
}
