package com.jar.app.feature.home.data.network

import android.app.Notification
import com.jar.app.feature.home.domain.model.*
import com.jar.app.feature.home.util.HomeConstants.Endpoints
import com.jar.app.feature.invoice.domain.model.InvoiceResp
import com.jar.app.feature.promo_code.domain.data.PromoCode
import com.jar.app.feature.survey.domain.model.SubmitSurveyResponse
import com.jar.app.feature.survey.domain.model.Survey
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonElement
import com.jar.app.core_base.util.BaseConstants.StaticContentType

internal class HomeDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun updateSession(appVersion: Int) = getResult<ApiResponseWrapper<Unit?>> {
        client.get {
            url(Endpoints.UPDATE_SESSION)
            parameter("appVersion", appVersion)
        }
    }

    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        getResult<ApiResponseWrapper<DashboardStaticData?>> {
            client.get {
                url(Endpoints.FETCH_DASHBOARD_STATIC_CONTENT)
                parameter("contentType", staticContentType.name)
            }
        }

    suspend fun fetchPublicStaticContent(
        staticContentType: StaticContentType,
        phoneNumber: String,
        context: String?
    ) =
        getResult<ApiResponseWrapper<DashboardStaticData?>> {
            client.get {
                url(Endpoints.FETCH_PUBLIC_STATIC_CONTENT)
                parameter("contentType", staticContentType.name)
                parameter("phoneNumber", phoneNumber)
                context?.let { parameter("context", it) }
            }
        }

    suspend fun fetchNotification(page: Int, size: Int) =
        getResult<ApiResponseWrapper<List<Notification>>> {
            client.get {
                url(Endpoints.FETCH_NOTIFICATION_LIST)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchInvoice(page: Int, size: Int) =
        getResult<ApiResponseWrapper<InvoiceResp>> {
            client.get {
                url(Endpoints.FETCH_INVOICE_LIST)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchPromoCode(page: Int, size: Int) =
        getResult<ApiResponseWrapper<List<PromoCode>>> {
            client.get {
                url(Endpoints.FETCH_PROMO_CODE_LIST)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun getSurvey() =
        getResult<ApiResponseWrapper<Survey?>> {
            client.get {
                url(Endpoints.FETCH_SURVEY)
            }
        }

    suspend fun submitSurvey(jsonElement: JsonElement) =
        getResult<ApiResponseWrapper<SubmitSurveyResponse>> {
            client.post {
                url(Endpoints.SUBMIT_SURVEY)
                setBody(jsonElement)
            }
        }

    suspend fun fetchDowntime() =
        getResult<ApiResponseWrapper<DowntimeResponse?>> {
            client.get {
                url(Endpoints.FETCH_DOWNTIME)
            }
        }

    suspend fun fetchActiveAnalyticsList() =
        getResult<ApiResponseWrapper<DashboardStaticData>> {
            client.get {
                url(Endpoints.FETCH_ACTIVE_ANALYTICS_LIST)
            }
        }

    suspend fun updateAdSourceData(adSourceData: AdSourceData) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_AD_DATA_SOURCE)
                setBody(adSourceData)
            }
        }

    suspend fun captureAppOpens() =
        getResult<ApiResponseWrapper<Boolean>> {
            client.get {
                url(Endpoints.CAPTURE_APP_OPENS)
            }
        }

    suspend fun fetchForceUpdateData() =
        getResult<ApiResponseWrapper<ForceUpdateResponse>> {
            client.get {
                url(Endpoints.FETCH_FORCE_UPDATE_DATA)
            }
        }

    suspend fun fetchIfKycIsRequired() =
        getResult<ApiResponseWrapper<IsKycRequiredData>> {
            client.get {
                url(Endpoints.FETCH_IS_KYC_REQUIRED)
            }
        }
}