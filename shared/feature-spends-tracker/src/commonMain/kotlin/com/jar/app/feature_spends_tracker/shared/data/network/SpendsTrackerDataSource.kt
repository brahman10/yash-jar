package com.jar.app.feature_spends_tracker.shared.data.network


import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data.SpendsTransactionData
import com.jar.app.feature_spends_tracker.shared.utils.SpendsTrackerConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class SpendsTrackerDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchSpendsData() = getResult<ApiResponseWrapper<SpendsData>> {
        client.get {
            url(Endpoints.FETCH_SPENDS_METADATA)
        }
    }

    suspend fun fetchSpendsTransactionData(page: Int, size: Int) =
        getResult<ApiResponseWrapper<List<SpendsTransactionData>>> {
            client.get {
                url(Endpoints.FETCH_SPENDS_LIST)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchSpendsEducationData() =
        getResult<ApiResponseWrapper<SpendsEducationData?>> {
            client.get {
                url(Endpoints.FETCH_SPENDS_EDUCATION_DATA)
            }
        }

    suspend fun reportTransaction(reportTransactionRequest: ReportTransactionRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.REPORT_TRANSACTION)
                setBody(reportTransactionRequest)
            }
        }
}