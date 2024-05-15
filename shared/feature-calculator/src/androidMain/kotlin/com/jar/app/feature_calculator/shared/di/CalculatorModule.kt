package com.jar.app.feature_calculator.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_calculator.shared.data.network.CalculatorDataSource
import com.jar.app.feature_calculator.shared.data.repository.CalculatorRepository
import com.jar.app.feature_calculator.shared.domain.repository.CalculatorRepositoryImpl
import com.jar.app.feature_calculator.shared.domain.use_case.FetchCalculatorDataUseCase
import com.jar.app.feature_calculator.shared.domain.use_case.impl.FetchCalculatorDataUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class CalculatorModule {

    @Provides
    @Singleton
    internal fun provideCalculatorDataSource(@AppHttpClient client: HttpClient): CalculatorDataSource {
        return CalculatorDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideCalculatorRepository(calculatorDataSource: CalculatorDataSource): CalculatorRepository {
        return CalculatorRepositoryImpl(calculatorDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchCalculatorDataUseCase(calculatorRepository: CalculatorRepository): FetchCalculatorDataUseCase {
        return FetchCalculatorDataUseCaseImpl(calculatorRepository)
    }
}