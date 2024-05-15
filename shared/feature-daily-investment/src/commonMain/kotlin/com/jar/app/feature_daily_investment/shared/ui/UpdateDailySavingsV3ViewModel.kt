package com.jar.app.feature_daily_investment.shared.ui


import com.jar.app.core_base.util.BaseConstants.ButtonType
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_daily_investment.shared.util.EventKey.DSRecAmount
import com.jar.app.feature_daily_investment.shared.util.EventKey.DsUpdateFlow_PageClicked
import com.jar.app.feature_daily_investment.shared.util.EventKey.DsUpdateFlow_Shown
import com.jar.app.feature_daily_investment.shared.util.EventKey.False
import com.jar.app.feature_daily_investment.shared.util.EventKey.PaymentUPISelected
import com.jar.app.feature_daily_investment.shared.util.EventKey.True
import com.jar.app.feature_daily_investment.shared.util.EventKey.isDefault
import com.jar.app.feature_daily_investment.shared.util.EventKey.isUpdatedGreaterthanCurrent
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingsUpdateFlowValues
import com.jar.app.feature_daily_investment.shared.domain.model.UpdateDailyInvestmentStaticData
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchUpdateDailyInvestmentStaticDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.util.EventKey.DSCurAmount
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UpdateDailySavingsV3ViewModel constructor(
    private val fetchUpdateDailyInvestmentStaticDataUseCase: FetchUpdateDailyInvestmentStaticDataUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val analyticsHandler: AnalyticsApi,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticUpdateFlowData: MutableStateFlow<RestClientResult<ApiResponseWrapper<UpdateDailyInvestmentStaticData?>>> = MutableStateFlow(
        RestClientResult.loading())
    val staticUpdateFlowData: CStateFlow<RestClientResult<ApiResponseWrapper<UpdateDailyInvestmentStaticData?>>> = _staticUpdateFlowData.toCommonStateFlow()

    private val _isAutoPayResetRequiredFlowData:
            MutableStateFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>> = MutableStateFlow(
        RestClientResult.none())
    val isAutoPayResetRequiredFlowData: CStateFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>> =
        _isAutoPayResetRequiredFlowData.toCommonStateFlow()

    private val _updateDailySavingStatusFlowData :
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>> = MutableStateFlow(
            RestClientResult.none())
    val updateDailySavingStatusFlowData: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>> =
        _updateDailySavingStatusFlowData.toCommonStateFlow()

    private val _dailySavingsValuesFlow =
        MutableStateFlow<DailySavingsUpdateFlowValues?>(null)
    val dailySavingsValuesFlow: StateFlow<DailySavingsUpdateFlowValues?> = _dailySavingsValuesFlow

    fun fetchUpdateDailyInvestmentStaticData(){
        viewModelScope.launch {
            fetchUpdateDailyInvestmentStaticDataUseCase.fetchUpdateDailyInvestmentStaticData().collect {
                _staticUpdateFlowData.emit(it)
            }
        }
    }

    fun enableOrUpdateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusFlowData.emit(it)
                }
        }
    }

    fun enableAutomaticDailySavings() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.DAILY_SAVINGS,
                enableAutoSave = true
            ).collect {}
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredFlowData.emit(it)
            }
        }
    }
    fun updateDailySavingsFlowValues(data : UpdateDailyInvestmentStaticData?, updatedRecommendedValue : Float?){
        viewModelScope.launch {
            if (data != null) {
                _dailySavingsValuesFlow.emit(
                    DailySavingsUpdateFlowValues(
                        currentDailySavingsAmount = data.currentDailySavingsAmount,
                        recommendedDailySavingsAmount = data.dsRecommendedAmount,
                        currentDailySavingsProjection = data.currentDailySavingsAmount?.times(7),
                        recommendedDailySavingsProjection = data.dsRecommendedAmount?.times(7),
                    )
                )
            }
            else {
                _dailySavingsValuesFlow.emit(
                    DailySavingsUpdateFlowValues(
                        currentDailySavingsAmount = _dailySavingsValuesFlow.value?.currentDailySavingsAmount,
                        recommendedDailySavingsAmount = updatedRecommendedValue,
                        currentDailySavingsProjection = _dailySavingsValuesFlow.value?.currentDailySavingsProjection,
                        recommendedDailySavingsProjection = updatedRecommendedValue?.times(7),
                    )
                )
            }
        }
    }

    fun postShownEvent(dsCurrent: Int, dsRecommended: Int, suggestedUpiApp: String){
        analyticsHandler.postEvent(
            DsUpdateFlow_Shown,
            mapOf(DSCurAmount to dsCurrent.toString(),
                DSRecAmount to dsRecommended.toString(),
                PaymentUPISelected to suggestedUpiApp
            )
        )
    }

    fun postClickEvent(buttonType : String?, defaultAmount: Float?){
        analyticsHandler.postEvent(
            DsUpdateFlow_PageClicked,
            mapOf(ButtonType to buttonType.toString(),
                isUpdatedGreaterthanCurrent to if(_dailySavingsValuesFlow.value?.currentDailySavingsAmount.orZero().toInt() < _dailySavingsValuesFlow.value?.recommendedDailySavingsAmount.orZero().toInt()) True else False,
                isDefault to if(defaultAmount.orZero().toInt() == _dailySavingsValuesFlow.value?.recommendedDailySavingsAmount.orZero().toInt()) True else False
            )
        )
    }



}