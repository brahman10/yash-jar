package com.jar.app.feature_calculator.shared.domain.use_case.impl

import com.jar.app.feature_calculator.shared.data.repository.CalculatorRepository
import com.jar.app.feature_calculator.shared.domain.use_case.FetchCalculatorDataUseCase

internal class FetchCalculatorDataUseCaseImpl constructor(
    private val calculatorRepository: CalculatorRepository
) : FetchCalculatorDataUseCase {

    override suspend fun fetchCalculatorData(calculatorType: String) = calculatorRepository.fetchCalculatorData(calculatorType)

}