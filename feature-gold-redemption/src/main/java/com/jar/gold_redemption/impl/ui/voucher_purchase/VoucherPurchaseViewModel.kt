package com.jar.gold_redemption.impl.ui.voucher_purchase


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiateOrderUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherVoucherDetailUseCase
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherProducts
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseAPIData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

enum class WhichBottomSheet {
    AMOUNT, STATE_DETAIL
}

@HiltViewModel
internal class VoucherPurchaseViewModel @Inject constructor(
    private val voucherDiscoveryUseCase: VoucherVoucherDetailUseCase,
    private val voucherOrderPlacementUseCase: VoucherInitiateOrderUseCase
) : ViewModel() {

    private val _faqListLiveData = MutableLiveData<List<ExpandableCardModel>>()
    val faqListLiveData: LiveData<List<ExpandableCardModel>>
        get() = _faqListLiveData
    var peakQuantity: Int = 4
    var discountPercentage: Float = 0f
    var errorTextHidden: String? = null

    private val _voucherPurchase = MutableLiveData<VoucherPurchaseAPIData?>()
    val voucherPurchase: LiveData<VoucherPurchaseAPIData?> = _voucherPurchase

    private val _voucherCardType = MutableLiveData<CardType>()
    val voucherCardType: LiveData<CardType> = _voucherCardType

    private val _quantity = MutableLiveData<Int>(1)
    val quantity: LiveData<Int> = _quantity

    private val _whichBottomSheet = MutableLiveData<WhichBottomSheet>(WhichBottomSheet.AMOUNT)
    val whichBottomSheet: LiveData<WhichBottomSheet> = _whichBottomSheet

    private val _errorText = MutableLiveData<String?>(null)
    val errorText: LiveData<String?> = _errorText

    private val _brandName = MutableLiveData<String?>(null)
    val brandName: LiveData<String?> = _brandName

    private val _goldGreenBannerString = MutableLiveData<String?>(null)
    val goldGreenBannerString: LiveData<String?> = _goldGreenBannerString

    private val _totalAmount = MutableLiveData<Float>(500f)
    val totalAmount: LiveData<Float> = _totalAmount

    private val _amountList = MutableLiveData<List<Float?>>(listOf())
    val amountList: LiveData<List<Float?>> = _amountList

    private val _selectedAmount = MutableLiveData<Float>(-1f)
    val selectedAmount: LiveData<Float> = _selectedAmount

    private val _isMinusEnabled = MutableLiveData<Boolean>(false)
    val isMinusEnabled: LiveData<Boolean> = _isMinusEnabled

    private val _isPlusEnabled = MutableLiveData<Boolean>(false)
    val isPlusEnabled: LiveData<Boolean> = _isPlusEnabled

    private val _voucherData = MutableLiveData<VoucherProducts?>()
    val voucherData: LiveData<VoucherProducts?> = _voucherData

    private val _placeOrderAPILiveData =
        SingleLiveEvent<Pair<InitiatePaymentResponse, GoldRedemptionInitiateCreateOrderRequest>>()
    val placeOrderAPILiveData: LiveData<Pair<InitiatePaymentResponse, GoldRedemptionInitiateCreateOrderRequest>>
        get() = _placeOrderAPILiveData

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _showToast = MutableLiveData<String>("")
    val showToast: LiveData<String> = _showToast

    fun fetchVoucherPurchase(voucherId: String, weakReference: WeakReference<Context?>) {
        viewModelScope.launch {
            voucherDiscoveryUseCase.fetchVoucherDetail(voucherId).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.let {
                        _voucherPurchase.value = it
                    }
                    peakQuantity = it?.peakQuantity ?: 4
                    _amountList.value = it?.amountList?.toMutableList() ?: listOf<Float>()
                    _selectedAmount.value = it?.amountList?.getOrNull(0)
                    _faqListLiveData.value = curateVoucherPurchaseFaqs(
                        it?.howToRedeem,
                        it?.tnc,
                        null,
                        weakReference = weakReference,
                        showDrawable = true
                    )
                    _brandName.value = it?.brandTitle
                    discountPercentage = it?.discountPercentage.orZero()
                    _voucherCardType.value = it?.getCardType()
                    resetValues(weakReference)
                }
            )
        }
    }

    fun resetValues(weakReference: WeakReference<Context?>) {
        _isMinusEnabled.value = _quantity.value != 1
        val totalAMountOnNext = _selectedAmount?.value?.times(_quantity?.value.orZero() + 1).orZero()
        val wouldNextQuantityValid = totalAMountOnNext <= 100000
        _isPlusEnabled.value = _quantity.value.orZero() < peakQuantity && wouldNextQuantityValid

        val totalAmount = _selectedAmount?.value?.times(_quantity?.value.orZero()).orZero()
        _totalAmount.value = totalAmount

        errorTextHidden = if (!wouldNextQuantityValid) weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_maximum_voucher_limit_is_one_lakh).orEmpty() else null
        _quantity.value?.let {
             if (it >= peakQuantity) errorTextHidden = weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_only_four_allowed).orEmpty()
        }
        if (discountPercentage > 0) {
            val fl = _totalAmount.value.orZero() * discountPercentage / 100
            _goldGreenBannerString.value = weakReference.get()?.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_gold_worth_amount_will_be_added_to_your_jar_locker, fl.orZero()).orEmpty()
        } else {
            _goldGreenBannerString.value = null
        }
        _errorText.value = ""
    }

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }

    fun initiatePlaceOrderRequest(param: GoldRedemptionInitiateCreateOrderRequest) {
        viewModelScope.launch {
            voucherOrderPlacementUseCase.initiateOrder(param).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.let {
                        _placeOrderAPILiveData.value = Pair(it, param)
                    } ?: run {
                        _showToast.value = "Something went wrong"
                    }
                },
                onError = { message, _ ->
                    _showToast.value = message
                    showLoading(false)
                }
            )
        }
    }

    fun onMinusClick(weakReference: WeakReference<Context?>) {
        _quantity.value = _quantity.value?.minus(1)
        resetValues(weakReference)
    }

    fun onAddClick(weakReference: WeakReference<Context?>) {
        _quantity.value = _quantity.value?.plus(1)
        resetValues(weakReference)
    }

    fun setSelectedAmount(it1: Float) {
        _quantity.value = 1
        _selectedAmount.value = it1
    }

    fun setWhichBottomSheet(amount: WhichBottomSheet) {
        _whichBottomSheet.value = amount
    }

    fun setLoading(b: Boolean) {
        _showLoading.value = b
    }

    fun clearVoucher() {
//        _voucherCardetail.value = null
//        _voucherPurchase.value = null
//        _voucherData.value = null
//        discountPercentage = 0f
//        _faqListLiveData.value = listOf()
    }

    fun setFromHidden() {
        errorTextHidden?.let {
            _errorText.value = it
        }
    }

    fun setGreenGold(it: String) {
        _goldGreenBannerString.value = it
    }
}
