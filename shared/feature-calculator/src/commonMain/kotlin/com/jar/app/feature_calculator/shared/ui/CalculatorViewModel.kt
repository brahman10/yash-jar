package com.jar.app.feature_calculator.shared.ui

import com.jar.app.feature_calculator.shared.domain.model.CalculatorDataRes
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import com.jar.app.feature_calculator.shared.domain.model.SliderData
import com.jar.app.feature_calculator.shared.domain.model.SliderSubType
import com.jar.app.feature_calculator.shared.domain.use_case.FetchCalculatorDataUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.roundToInt

class CalculatorViewModel constructor(
    private val fetchCalculatorDataUseCase: FetchCalculatorDataUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _uiStateFlow = MutableStateFlow(CalculatorUiState())
    val uiStateFlow: StateFlow<CalculatorUiState>
        get() = _uiStateFlow.asStateFlow()

    private var tenureType = SliderSubType.YEAR

    private var parentList: List<SliderData>? = null

    fun fetchData(calculatorType: CalculatorType) {
        viewModelScope.launch {
            fetchCalculatorDataUseCase.fetchCalculatorData(calculatorType.name).collect { response ->
                    withContext(Dispatchers.Default) {
                        parentList = response.data?.data?.sliderData
                        if (response.status == RestClientResult.Status.SUCCESS)
                            delay(1500)//Intentional
                        _uiStateFlow.update {
                            it.copy(
                                isLoading = response.status == RestClientResult.Status.LOADING,
                                data = response.data?.data,
                                sliderList = response.data?.data?.sliderData?.filter {
                                    if (tenureType == SliderSubType.YEAR)
                                        it.getSliderSubType() != SliderSubType.MONTH
                                    else
                                        it.getSliderSubType() != SliderSubType.YEAR
                                },
                                errorString = if (response.status == RestClientResult.Status.ERROR) response.message else null
                            )
                        }
                    }
                }
        }
    }

    fun onValueChanged(amount: Int, tenure: Int, interest: Float, calculatorType:CalculatorType) {
        viewModelScope.launch(Dispatchers.Default) {
            val amountData = parentList?.find { it.getSliderSubType() == SliderSubType.AMOUNT }
            val tenureData = parentList?.find { it.getSliderSubType() == tenureType }
            val interestData = parentList?.find { it.getSliderSubType() == SliderSubType.PERCENTAGE }

            var isAnyValueInvalid = false

            val filteredAmount = if ((amount < amountData!!.min || amount > amountData.max)) {
                isAnyValueInvalid = true
                0
            } else amount
            val filteredTenure = if ((tenure < tenureData!!.min || tenure > tenureData.max)) {
                isAnyValueInvalid = true
                0
            } else tenure
            val filteredInterest = if ((interest < interestData!!.min || interest > interestData.max)) {
                    isAnyValueInvalid = true
                    0f
                } else interest

            if (isAnyValueInvalid) {
                _uiStateFlow.update {
                    it.copy(
                        calculatedData = CalculatedData(
                            filteredAmount, filteredTenure, filteredInterest, 0f, tenureType = tenureType
                        )
                    )
                }
            } else {

               if (calculatorType == CalculatorType.SAVINGS_CALCULATOR) {
                   var amountForCalculation: Int
                   val timesCompoundedPerYear = 1.0  // Yearly compounding
                    val annualInterestRate: Double = interest.toDouble() / 100
                    val tenureInYears: Double = if (tenureType == SliderSubType.YEAR) {
                        amountForCalculation = amount * 12 * tenure * 30
                        tenure.toDouble()
                    } else {
                        amountForCalculation = amount * tenure * 30
                        tenure.toDouble() / 12
                    }

                    val futureValue: Double = amountForCalculation * (1 + annualInterestRate / timesCompoundedPerYear).pow(timesCompoundedPerYear * tenureInYears)
                    val interestEarned = (futureValue - amountForCalculation).toFloat()   // Interest earned
                    _uiStateFlow.update {
                        it.copy(
                            calculatedData = CalculatedData(
                                interestEarned.roundToInt(), tenure, interest, (amountForCalculation + interestEarned), tenureType = tenureType
                            )
                        )
                    }
                } else {
                    val r: Double = interest.toDouble() / (12 * 100)//Annual interest to monthly interest, % value to real value
                    val t: Double = ((if (tenureType == SliderSubType.YEAR) (tenure * 12) else tenure).toDouble() / 12 * 12)
                    // Calculate EMI
                    val result = amount * r / (1 - ((1 + r).pow(-t))).toFloat()
                    _uiStateFlow.update {
                        it.copy(
                            calculatedData = CalculatedData(
                                amount, tenure, interest, result.toFloat(), tenureType = tenureType
                            )
                        )
                    }
                }

            }
        }
    }

    fun onTenureChange(tenureType: SliderSubType, amount: Int, interest: Float,calculatorType: CalculatorType) {
        this@CalculatorViewModel.tenureType = tenureType
        viewModelScope.launch(Dispatchers.Default) {
            _uiStateFlow.update {
                it.copy(
                    sliderList = parentList?.filter {
                        if (tenureType == SliderSubType.YEAR)
                            it.getSliderSubType() != SliderSubType.MONTH
                        else
                            it.getSliderSubType() != SliderSubType.YEAR
                    },
                )
            }

            onValueChanged(amount, (parentList?.findLast { it.getSliderSubType() == tenureType }?.min ?: 1f).toInt(), interest,calculatorType)
        }
    }

    fun syncAnalyticsEvent(eventName: String, data: Map<String, Any>?, syncOncePerSession: Boolean) {
        data?.let {
            analyticsApi.postEvent(
                event = eventName,
                values = it,
                shouldPushOncePerSession = syncOncePerSession
            )
        } ?: kotlin.run {
            analyticsApi.postEvent(
                eventName = eventName,
                shouldPushOncePerSession = syncOncePerSession
            )
        }
    }
}

data class CalculatorUiState(
    val isLoading: Boolean = false,
    val data: CalculatorDataRes? = null,
    val sliderList: List<SliderData>? = null,
    val calculatedData: CalculatedData? = null,
    val errorString: String? = null
)

data class CalculatedData(
    val amount: Int? = 0,
    val tenure: Int? = 0,
    val interest: Float? = 0.0f,
    val finalAmount: Float? = 0.0f,
    val tenureType: SliderSubType = SliderSubType.YEAR
)