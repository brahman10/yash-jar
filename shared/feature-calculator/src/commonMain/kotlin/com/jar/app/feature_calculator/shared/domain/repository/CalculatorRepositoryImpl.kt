package com.jar.app.feature_calculator.shared.domain.repository

import com.jar.app.feature_calculator.shared.data.network.CalculatorDataSource
import com.jar.app.feature_calculator.shared.data.repository.CalculatorRepository

class CalculatorRepositoryImpl constructor(
    private val calculatorDataSource: CalculatorDataSource
) : CalculatorRepository {

    override suspend fun fetchCalculatorData(calculatorType: String) = getFlowResult {
        calculatorDataSource.fetchCalculatorData(calculatorType)
    }
}