package com.jar.android.feature_post_setup.impl.ui.failed_transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.android.feature_post_setup.impl.model.CalendarDayStatus
import com.jar.android.feature_post_setup.impl.model.getDayStatus
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarDataResp
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupCalenderDataUseCase
import com.jar.app.feature_post_setup.domain.use_case.InitiateFailedPaymentsUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FailedTransactionViewModel @Inject constructor(
    private val calendarUtil: CalendarUtil,
    private val fetchPostSetupCalenderDataUseCase: FetchPostSetupCalenderDataUseCase,
    private val initiateFailedPaymentsUseCase: InitiateFailedPaymentsUseCase,
) : ViewModel() {

    private val _calenderLiveData = SingleLiveEvent<RestClientResult<List<FeaturePostSetUpCalendarInfo>>>()
    val calenderLiveData: LiveData<RestClientResult<List<FeaturePostSetUpCalendarInfo>>>
        get() = _calenderLiveData

    private val failedTransactionsList = ArrayList<SelectedTransactions>()

    var calenderDataResp: CalendarDataResp? = null
    private val _selectedFailedDaysLiveData = SingleLiveEvent<List<SelectedTransactions>>()
    val selectedFailedDaysLiveData: LiveData<List<SelectedTransactions>>
        get() = _selectedFailedDaysLiveData

    private val _failedPaymentLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>()
    val failedPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
        get() = _failedPaymentLiveData

    fun fetchPostSetupCalenderData(monthIndex: Int) {
        _calenderLiveData.postValue(RestClientResult.loading())
        viewModelScope.launch {
            fetchPostSetupCalenderDataUseCase.fetchPostSetupCalendarData(
                startDate = calendarUtil.getMonthFirstDay(monthIndex),
                endDate = calendarUtil.getMonthLastDay(monthIndex)
            ).collect(
                onSuccess = {
                    calenderDataResp = it
                    it.calendarInfo.let {
                        failedTransactionsList.clear()
                        val list = calendarUtil.createMonthCalendar(monthIndex, it)
                        list.map {
                            if (it.getDayStatus() == CalendarDayStatus.FAILED) it.isSelected = true
                        }
                        _calenderLiveData.postValue(RestClientResult.success(list))
                        list.filter { it.getDayStatus() == CalendarDayStatus.FAILED }.let {
                            it.map {
                                failedTransactionsList.add(
                                    SelectedTransactions(it.amount.orZero(), it.id!!, true)
                                )
                            }
                        }
                        _selectedFailedDaysLiveData.postValue(failedTransactionsList)
                    }
                }
            )
        }
    }

    fun updateFailedTransactionSelection(list: List<FeaturePostSetUpCalendarInfo>, id: String?, position: Int) {
        viewModelScope.launch {
            val newList = ArrayList(list.map { it.copy() })
            val item = newList[position]
            if (item.getDayStatus() == CalendarDayStatus.FAILED && item.id == id) {
                item.isSelected = item.isSelected.orFalse().not()
                newList[position] = item
                _calenderLiveData.postValue(RestClientResult.success(newList))
                failedTransactionsList.find { it.roundOffId == item.id }?.let {
                    it.isSelected = item.isSelected.orFalse()
                }
                _selectedFailedDaysLiveData.postValue(failedTransactionsList)
            }
        }
    }

    fun initiateFailedPayment(
        amount: Float,
        paymentProvider: String,
        roundOffIds: List<String>
    ) {
        viewModelScope.launch {
            initiateFailedPaymentsUseCase.initiatePaymentForFailedTransactions(
                amount = amount,
                paymentProvider = paymentProvider,
                type = SavingsType.DAILY_SAVINGS.name,
                roundOffsLinked = roundOffIds
            ).collect {
                _failedPaymentLiveData.postValue(it)
            }
        }
    }

    class SelectedTransactions(
        val amount: Float,
        val roundOffId: String,
        var isSelected: Boolean
    )
}