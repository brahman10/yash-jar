package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_breakdown

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingsBreakdownData
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.DAYS_IN_MONTH
import com.jar.app.feature_daily_investment.impl.domain.model.DailyInvestmentProjectionBreakdownData
import com.jar.app.feature_daily_investment.impl.util.DailySavingPredictionUtil
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsV2BreakdownViewModel @Inject constructor(
    private val dailySavingsBreakdownGenerator: DailySavingsV2BreakdownGenerator,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val dailySavingPredictionUtil: DailySavingPredictionUtil
) : ViewModel() {

    private var job: Job? = null

    private val _dailySavingsBreakdownBottomSheetLiveData =
        SingleLiveEvent<DailySavingsBreakdownData>()
    val dailySavingsBreakdownBottomSheetLiveData: LiveData<DailySavingsBreakdownData>
        get() = _dailySavingsBreakdownBottomSheetLiveData

    private val _fetchCurrentGoldPriceResponse =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val fetchCurrentGoldPriceResponse: LiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>> =
        _fetchCurrentGoldPriceResponse

    private val _totalGoldVolumeProjectionLiveData =
        MutableLiveData<DailyInvestmentProjectionBreakdownData>()
    val totalGoldVolumeProjectionLiveData: LiveData<DailyInvestmentProjectionBreakdownData>
        get() = _totalGoldVolumeProjectionLiveData

    fun fetchGoldPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collect {
                    _fetchCurrentGoldPriceResponse.postValue(it)
                }
        }
    }

    fun calculateGoldAmount(
        context: WeakReference<Context>,
        months: Int,
        dailyInvestment: Float
    ) {
        viewModelScope.launch {
            val dailySavingPrediction = dailySavingPredictionUtil.generateDailySavingPrediction(
                months = months,
                currentPrice = fetchCurrentGoldPriceResponse.value?.data?.data,
                dailyInvestment = dailyInvestment
            )
            val goldWorthBreakdownData = dailySavingsBreakdownGenerator.generateBottomSheetData(
                contextRef = context,
                totalInvestedAmount = dailySavingPrediction.totalInvestment,
                totalInvestedAmountAfterTax = dailySavingPrediction.totalInvestmentAfterTax,
                totalAmountSavedAfterAppreciation = dailySavingPrediction.totalInvestmentAfterAppreciation,
                totalNumberOfDays = months * DAYS_IN_MONTH,
                dailyInvestment = dailyInvestment,
                totalGramsOfGold = dailySavingPrediction.totalInvestmentInVolume,
                gstOnTotalAmountSaved = dailySavingPrediction.gstOnTotalAmountSaved
            )
            _dailySavingsBreakdownBottomSheetLiveData.postValue(goldWorthBreakdownData)
        }
    }
}