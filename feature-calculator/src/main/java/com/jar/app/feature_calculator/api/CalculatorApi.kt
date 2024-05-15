package com.jar.app.feature_calculator.api

import com.jar.app.feature_calculator.shared.domain.model.CalculatorType

/**
 * Api to be used by other modules
 **/
interface CalculatorApi {

    fun openCalculatorScreen(calculatorType: CalculatorType, fromScreen: String?, fromSection: String?)

}