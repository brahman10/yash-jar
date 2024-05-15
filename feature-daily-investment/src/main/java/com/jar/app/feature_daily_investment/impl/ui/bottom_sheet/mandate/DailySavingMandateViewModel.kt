package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.mandate

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.impl.domain.data.DailySavingsMandateInfoData
import com.jar.app.feature_daily_investment.impl.domain.data.DailySavingsMandatePaymentOption
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentMandateBottomSheetData
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSMandateDataUseCase
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiAppPaymentPageItem
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@HiltViewModel
internal class DailySavingMandateViewModel @Inject constructor(
    private val fetchDSMandateDataUseCase: FetchDSMandateDataUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    mApp: Application
) : AndroidViewModel(mApp) {

    private val packageManager = mApp.packageManager

    private val _bottomSheetLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentMandateBottomSheetData?>>>()
    val bottomSheetLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentMandateBottomSheetData?>>> get() = _bottomSheetLiveData

    private val _mandateInfoLiveData =
        MutableLiveData<List<DailySavingsMandateInfoData>?>()
    val mandateInfoLiveData: LiveData<List<DailySavingsMandateInfoData>?> get() = _mandateInfoLiveData

    private val _paymentOptionLiveData =
        MutableLiveData<List<DailySavingsMandatePaymentOption>?>()
    val paymentOptionLiveData: LiveData<List<DailySavingsMandatePaymentOption>?> get() = _paymentOptionLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    fun getPreSelectedUpi(): String? {
        return if (paymentOptionLiveData.value.isNullOrEmpty().not())
            paymentOptionLiveData.value?.getOrNull(0)?.packageName
        else
            null
    }

    fun fetchBottomSheetData() {
        viewModelScope.launch {
            fetchDSMandateDataUseCase.fetchDSMandateBSData().collect {
                _bottomSheetLiveData.postValue(it)
            }
        }
    }

    fun createMandateInfoList(
        dailySavingsValue: String,
        otherMandateName: String? = null,
        otherMandateValue: String? = null
    ) {
        val mandateInfoList = mutableListOf<DailySavingsMandateInfoData>()

        val dailySavingsMandateItem = DailySavingsMandateInfoData(
            mandateType = "Daily Savings",
            value = dailySavingsValue,
            isCurrentMandate = true,
            status = null
        )
        mandateInfoList.add(dailySavingsMandateItem)

        if (otherMandateName.isNullOrEmpty().not()) {
            val otherMandateItem = DailySavingsMandateInfoData(
                mandateType = otherMandateName!!,
                value = otherMandateValue!!,
                isCurrentMandate = false,
                status = "Active"
            )
            mandateInfoList.add(otherMandateItem)
        }
        _mandateInfoLiveData.postValue(mandateInfoList)
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
            }
        }
    }

    fun updateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount)
                .collect(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                    }
                )
        }
    }

    fun createPaymentOptionList(
        data: List<BasePaymentPageItem>
    ) {
        val paymentOptionList = mutableListOf<DailySavingsMandatePaymentOption>()

        data.forEach {
            if (it is UpiAppPaymentPageItem) {
                val upiApp = getUpiAppFromPackageName(
                    it.upiAppPackageName,
                    packageManager
                )
                val dailySavingsMandateItem = DailySavingsMandatePaymentOption(
                    packageName = upiApp.packageName,
                    optionIcon = upiApp.icon!!,
                    optionName = upiApp.appName
                )
                paymentOptionList.add(dailySavingsMandateItem)
            }

        }
        _paymentOptionLiveData.postValue(paymentOptionList)
    }

    private fun getUpiAppFromPackageName(
        packageName: String,
        packageManager: PackageManager
    ): UpiApp {
        return UpiApp(
            packageName = packageName,
            icon = packageManager.getApplicationIcon(packageName),
            appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
        )
    }
}