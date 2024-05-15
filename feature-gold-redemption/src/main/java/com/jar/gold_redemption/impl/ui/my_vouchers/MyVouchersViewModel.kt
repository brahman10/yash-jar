package com.jar.gold_redemption.impl.ui.my_vouchers

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllMyVouchersUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPurchaseHistoryUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherUserVouchersCountUseCase
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject
@HiltViewModel
internal class MyVouchersViewModel @Inject constructor(
    private val voucherDiscoveryUseCase: VoucherAllMyVouchersUseCase,
    private val voucherOrderPlacementUseCase: VoucherPurchaseHistoryUseCase,
    private val voucherUserVouchersCountUseCase: VoucherUserVouchersCountUseCase,
) : ViewModel() {

    private val _voucherList = MutableLiveData<List<UserVoucher?>>()
    val voucherList: LiveData<List<UserVoucher?>> = _voucherList

    private val _paymentHistoryList = MutableLiveData<List<PurchaseItemData?>>()
    val paymentHistoryList: LiveData<List<PurchaseItemData?>> = _paymentHistoryList

    val horizontalFilterList: SnapshotStateList<String> = mutableStateListOf<String>(
        "Active",
        "All",
    )

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _selectedIndex = MutableLiveData<FetchVoucherType>(FetchVoucherType.ACTIVE)
    val selectedIndex: LiveData<FetchVoucherType> = _selectedIndex

    private val _showToast = MutableLiveData<String>("")
    val showToast: LiveData<String> = _showToast

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }

    fun fetchAllMyVouchers() {
        viewModelScope.launch {
            voucherDiscoveryUseCase.fetchAllMyVouchers(_selectedIndex.value).collect(
                onLoading = {
                    showLoading(true)
                },
                onError = { message, _ ->
                    showLoading(false)
                    _showToast.value = message
                },
                onSuccess = {
                    showLoading(false)
                    it?.userVoucherList?.takeIf { !it.isNullOrEmpty() }?.let {
                        _voucherList.value = it
                    } ?: run {
                        _voucherList.value = emptyList()
                    }

                }
            )
        }
    }

    fun fetchPurchaseHistory() {
        viewModelScope.launch {
            voucherOrderPlacementUseCase.fetchPurchaseHistory().collect(
                onLoading = {
                    showLoading(true)
                },
                onError = { message, _ ->
                    showLoading(false)
                    _showToast.value = message
                },
                onSuccess = {
                    it?.voucherPaymentObjectList?.let {
                        _paymentHistoryList.value = it
                    }
                })
        }
    }

    fun fetchActiveCount(weakReference: WeakReference<Context?>) {
        viewModelScope.launch {
            voucherUserVouchersCountUseCase.fetchUserVouchersCount().collect(
                onLoading = {
                    showLoading(true)
                },
                onError = {message, _ ->
                    showLoading(false)
                    _showToast.value = message
                },
                onSuccess = {
                    showLoading(false)
                    if (it?.activeVouchersCount.orZero() == 0 && it?.allVouchersCount.orZero() == 0) {
                        horizontalFilterList.clear()
                    } else if (!horizontalFilterList.isEmpty()) {
                        horizontalFilterList[0] = weakReference.get()?.getString(
                            com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_active_d,
                            it?.activeVouchersCount.orZero()
                        ).orEmpty()
                        horizontalFilterList[1] = weakReference.get()?.getString(
                            com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_all_d,
                            it?.allVouchersCount.orZero()
                        ).orEmpty()
                    } else {
                        horizontalFilterList.addAll(
                            arrayOf(
                                weakReference.get()?.getString(
                                    com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_active_d,
                                    it?.activeVouchersCount.orZero()
                                ).orEmpty(),
                                weakReference.get()?.getString(
                                    com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_all_d,
                                    it?.allVouchersCount.orZero()
                                ).orEmpty(),
                            )
                        )
                    }
                }
            )
        }
    }

    fun setActive(it: FetchVoucherType) {
        _selectedIndex.value = it
    }
}
