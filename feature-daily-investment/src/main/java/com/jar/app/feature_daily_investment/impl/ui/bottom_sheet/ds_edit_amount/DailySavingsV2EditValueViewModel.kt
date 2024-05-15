package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_edit_amount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class DailySavingsV2EditValueViewModel @Inject constructor(
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
): ViewModel(
) {
    private val _dsSeekBarLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()
    val dsSeekBarLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSeekBarLiveData

    private val _rVLiveData = MutableLiveData<List<SuggestedRecurringAmount>>()
    val rVLiveData: LiveData<List<SuggestedRecurringAmount>>
        get() = _rVLiveData

    fun fetchSeekBarData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS
            ).collect {
                _dsSeekBarLiveData.postValue(it)
            }
        }
    }

    fun createRvListData(savingSetupInfo: SavingSetupInfo) {
        val list = mutableListOf<SuggestedRecurringAmount>()
        savingSetupInfo.options.forEach {
            SuggestedRecurringAmount(it.amount,it.recommended)
            list.add(SuggestedRecurringAmount(it.amount,it.recommended))
        }
        _rVLiveData.postValue(list)
    }
}