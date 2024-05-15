package com.jar.app.feature_calculator.shared.data.network

import com.jar.app.feature_calculator.shared.domain.model.CalculatorDataRes
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import com.jar.app.feature_calculator.shared.util.CalculatorConstants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class CalculatorDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchCalculatorData(calculatorType: String) =
        getResult<ApiResponseWrapper<CalculatorDataRes?>> {
            if(calculatorType == CalculatorType.EMI_CALCULATOR.name) {
                client.get {
                    url(CalculatorConstants.Endpoints.FETCH_LENDING_CALCULATOR_DATA)
                    parameter("type", calculatorType)
                }
            }else{
                client.get {
                    url(CalculatorConstants.Endpoints.FETCH_GOLD_CALCULATOR_DATA)
                    parameter("type", calculatorType)
                }
            }
        }

}