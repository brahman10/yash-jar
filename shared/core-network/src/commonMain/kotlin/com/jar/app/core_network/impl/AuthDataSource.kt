package com.jar.app.core_network.impl

import com.jar.app.core_base.util.orZero
import com.jar.app.core_network.model.UpdateTokenWrapper
import com.jar.app.core_network.util.NetworkConstants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Constants
import com.jar.internal.library.jar_core_network.api.util.NetworkEventBus
import io.ktor.client.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.request.*

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.math.log

class AuthDataSource(private val client: HttpClient) : BaseDataSource() {

    private val INTERNAL_SERVER_ERROR = 400..510
    fun checkErrorCode(errorCode: String?): Boolean {
        return errorCode in setOf<String>(
            Constants.NetworkErrorCodes.INTERNET_NOT_WORKING.toString(),
            Constants.NetworkErrorCodes.NETWORK_CALL_CANCELLED.toString(),
        ) || (INTERNAL_SERVER_ERROR.contains(errorCode?.toIntOrNull().orZero()))
    }
    suspend fun refreshToken(refreshToken: String) =
        retryIOs(
            times = 15, initialDelay = 2000L, factor = 2.0, maxDelay = 20 * 1000,
            shouldRetry = {
                val result = it.status
                return@retryIOs (result == RestClientResult.Status.ERROR && checkErrorCode(
                    it.data?.errorCode?.orZero().toString()
                ))
            }, block = {
                getResult<ApiResponseWrapper<UpdateTokenWrapper>> {
                    client.post {
                        this.attributes.put(Auth.AuthCircuitBreaker, Unit)
                        url(NetworkConstants.RefreshToken.REFRESH_TOKEN_ENDPOINT)
                        setBody(JsonObject(mapOf(Pair("refreshToken", JsonPrimitive(refreshToken)))))
                    }
                }
            })
}