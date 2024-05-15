package com.jar.app.feature_calculator.shared.di

import com.jar.app.feature_calculator.shared.data.network.CalculatorDataSource
import com.jar.app.feature_calculator.shared.data.repository.CalculatorRepository
import com.jar.app.feature_calculator.shared.domain.repository.CalculatorRepositoryImpl
import com.jar.app.feature_calculator.shared.domain.use_case.FetchCalculatorDataUseCase
import com.jar.app.feature_calculator.shared.domain.use_case.impl.FetchCalculatorDataUseCaseImpl
import io.ktor.client.HttpClient

class CalculatorModule(
    client: HttpClient
) {

    private val calculatorDataSource: CalculatorDataSource by lazy {
        CalculatorDataSource(client)
    }

    private val calculatorRepository: CalculatorRepository by lazy {
        CalculatorRepositoryImpl(calculatorDataSource)
    }

    val fetchCalculatorDataUseCase: FetchCalculatorDataUseCase by lazy {
        FetchCalculatorDataUseCaseImpl(calculatorRepository)
    }
}