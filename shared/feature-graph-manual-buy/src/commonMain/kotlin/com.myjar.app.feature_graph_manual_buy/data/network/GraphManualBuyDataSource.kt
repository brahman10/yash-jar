package com.myjar.app.feature_graph_manual_buy.data.network

import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.myjar.app.feature_graph_manual_buy.data.model.CalanderModel
import com.myjar.app.feature_graph_manual_buy.data.model.CalenderBody
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse
import com.myjar.app.feature_graph_manual_buy.data.network.NetworkConstants.CalenderDataEndPoint
import com.myjar.app.feature_graph_manual_buy.data.network.NetworkConstants.FAQsEndPoint
import com.myjar.app.feature_graph_manual_buy.data.network.NetworkConstants.GraphDetailsEndPoint
import com.myjar.app.feature_graph_manual_buy.data.network.NetworkConstants.QuickActionsEndPoint
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.host
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GraphManualBuyDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchFaqs() =
        getResult<ApiResponseWrapper<FaqsResponse>> {
            client.get {
                url (FAQsEndPoint)
                parameter("contentType", "FAQS")
            }
        }

    suspend fun fetchQuickAction() =
        getResult<ApiResponseWrapper<QuickActionResponse>> {
            client.get {
                url (QuickActionsEndPoint)
                parameter("contentType", "MS_QUICK_ACTIONS")
            }
        }

    suspend fun fetchGraphData() =
        getResult<ApiResponseWrapper<GraphManualBuyPriceGraphModel>> {
            client.get {
                url (GraphDetailsEndPoint)
            }
        }

    suspend fun fetchCalenderData(startDate: String, endDate: String) =
        getResult<ApiResponseWrapper<CalanderModel>> {
            client.post() {
                url (CalenderDataEndPoint)
               setBody(
                   CalenderBody(
                   startDate,
                   endDate
               )
               )
            }
        }
}