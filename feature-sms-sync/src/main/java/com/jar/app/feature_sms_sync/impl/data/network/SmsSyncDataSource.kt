package com.jar.app.feature_sms_sync.impl.data.network

import com.jar.app.core_network.CoreNetworkBuildKonfig
import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest
import com.jar.app.feature_sms_sync.impl.utils.SmsSyncConstants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class SmsSyncDataSource constructor(
    private val client: HttpClient,
) : BaseDataSource() {

    suspend fun postData(
        body: SmsSyncRequest
    ) = getResult<ApiResponseWrapper<Unit?>> {
        client.post {
            host = SmsSyncConstants.SMS_PARSER_HOST_URL
            url("/${CoreNetworkBuildKonfig.BASE_URL_SMS_PARSER}/${SmsSyncConstants.Endpoint}")
            setBody(body)
        }
    }
}