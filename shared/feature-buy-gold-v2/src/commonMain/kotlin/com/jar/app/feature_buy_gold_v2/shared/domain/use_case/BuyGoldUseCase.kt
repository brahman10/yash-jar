package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface BuyGoldUseCase {

    suspend fun calculateVolumeFromAmount(
        amount: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null
    ): Flow<RestClientResult<Float>>

    suspend fun calculateVolumeFromAmountSync(
        amount: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null
    ): RestClientResult<Float>

    suspend fun calculateAmountFromVolume(
        volume: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null
    ): Flow<RestClientResult<Float>>

    suspend fun buyGoldByAmount(buyGoldByAmountRequest: BuyGoldByAmountRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

    suspend fun buyGoldByVolume(buyGoldByVolumeRequest: BuyGoldByVolumeRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

}