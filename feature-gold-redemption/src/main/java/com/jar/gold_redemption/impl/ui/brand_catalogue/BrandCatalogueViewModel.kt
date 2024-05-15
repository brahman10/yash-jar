package com.jar.gold_redemption.impl.ui.brand_catalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_gold_redemption.shared.data.network.model.AbandonScreenData
import com.jar.app.feature_gold_redemption.shared.data.network.model.BrandCatalogoueApiData
import com.jar.app.feature_gold_redemption.shared.data.network.model.ProductFilter
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherProducts
import com.jar.app.feature_gold_redemption.shared.data.network.model.OrderProcessingData
import com.jar.app.feature_gold_redemption.shared.data.network.model.PendingOrdersAPIResponse
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAbandonScreenUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllVouchersUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherBrandCatalogoueStaticUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPendingOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BrandCatalogueViewModel @Inject constructor(
    private val voucherDiscoveryUseCase: VoucherAbandonScreenUseCase,
    private val voucherFetchPendingUseCase: VoucherPendingOrdersUseCase,
    private val voucherBrandCatalougeStatic: VoucherBrandCatalogoueStaticUseCase,
    private val voucherAllVoucherUseCase: VoucherAllVouchersUseCase
) : ViewModel() {
    enum class WhichBottomSheet {
        ABANDON, VOUCHER_PROCESSING
    }

    private val _shouldRenderCollapsible = MutableLiveData<Boolean>(false)
    val shouldRenderCollapsible: LiveData<Boolean> = _shouldRenderCollapsible

    private val _voucherList = MutableLiveData<List<VoucherProducts?>>()
    val voucherList: LiveData<List<VoucherProducts?>> = _voucherList

    private val _paymentHistoryList = MutableLiveData<List<OrderProcessingData?>>(listOf(
        OrderProcessingData("ASDASD")
    ))
    val paymentHistoryList: LiveData<List<OrderProcessingData?>> = _paymentHistoryList

    private val _voucherCategoryList = MutableLiveData<List<ProductFilter?>>()
    val voucherCategoryList: LiveData<List<ProductFilter?>> = _voucherCategoryList

    private val _abandonScreenData = MutableLiveData<AbandonScreenData>()
    val abandonScreenData: LiveData<AbandonScreenData> = _abandonScreenData

    private val _pendingOrdersLD = MutableLiveData<PendingOrdersAPIResponse>()
    val pendingOrdersLD: LiveData<PendingOrdersAPIResponse> = _pendingOrdersLD

    private val _whichBottomSheet = MutableLiveData<WhichBottomSheet>(WhichBottomSheet.ABANDON)
    val whichBottomSheet: LiveData<WhichBottomSheet> = _whichBottomSheet

    private val _brandCatalogApidata = MutableLiveData<BrandCatalogoueApiData?>()
    val brandCatalogApidata: LiveData<BrandCatalogoueApiData?> = _brandCatalogApidata

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _showToast = MutableLiveData<String>("")
    val showToast: LiveData<String> = _showToast

    private val _myOrdersText = MutableLiveData<String>("My orders")
    val myOrdersText: LiveData<String> = _myOrdersText

    var tabName: String? = null
    fun fetchPendingOrders() {
        viewModelScope.launch {
            voucherFetchPendingUseCase.fetchPendingOrders().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it.let {
                        if (!it?.list.isNullOrEmpty()) {
                            _pendingOrdersLD.value = it
                            _whichBottomSheet.value = WhichBottomSheet.VOUCHER_PROCESSING
                        }
                    }
                },
                onError = { message, _ ->
                    _showToast.value = message
                }
            )
        }
    }

    fun fetchAbandonScreenData() {
        viewModelScope.launch {
            voucherDiscoveryUseCase.fetchAbandonScreen().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.let {
                        _abandonScreenData.value = it
                    }
                },
                onError = { message, _ ->
                    _showToast.value = message
                }
            )
        }
    }

    fun fetchIntroScreen() {
        viewModelScope.launch {
            voucherBrandCatalougeStatic.fetchBrandCatalogoueStatic().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    _brandCatalogApidata.value = it
                    _shouldRenderCollapsible.value = !it?.imageUrl.isNullOrBlank()
                },
                onError = { message, _ ->
                    _showToast.value = message
                }
            )
        }
    }

    fun fetchAllVouchers(value: String? = null) {
        tabName = value
        viewModelScope.launch {
            voucherAllVoucherUseCase.fetchAllVouchers(value).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.voucherProductsList?.let {
                        _voucherList.value = it
                    }
                    it?.productFilter?.let {
                        _voucherCategoryList.value = it
                    }
                    it?.myOrdersTxt?.let {
                        _myOrdersText.value = it
                    }
                },
                onError = { message, _ ->
                    _showToast.value = message
                }
            )
        }
    }

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }

    fun setWhichBottomSheet(abandon: WhichBottomSheet) {
        this._whichBottomSheet.value = abandon
    }

}
