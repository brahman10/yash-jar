package com.jar.app.feature_gold_price.shared.data.network

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceContext
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.util.Constants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class GoldPriceDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchCurrentGoldPrice(goldPriceType: GoldPriceType, goldPriceContext: GoldPriceContext? = null) =
        getResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>> {
            client.get {
                url(Constants.Endpoints.FETCH_CURRENT_GOLD_PRICE)
                parameter("type", goldPriceType.name)
                goldPriceContext?.let {
                    parameter("context", it.name)
                }
            }
        }
}