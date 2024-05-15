package com.jar.app.feature_calculator.shared.data.repository

import com.jar.app.feature_calculator.shared.domain.model.CalculatorDataRes
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface CalculatorRepository : BaseRepository {

    suspend fun fetchCalculatorData(calculatorType: String): Flow<RestClientResult<ApiResponseWrapper<CalculatorDataRes?>>>
}