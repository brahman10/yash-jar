package com.jar.app.feature.home.data.network

import com.jar.app.feature.home.domain.model.*
import com.jar.app.feature.home.util.UserConstants.Endpoints
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdownResponse
import com.jar.app.feature_settings.domain.model.VpaChips
import com.jar.app.feature_user_api.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class UserDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {
    suspend fun applyPromoCode(
        deviceDetails: DeviceDetails,
        promoCode: String,
        id: String? = null,
        type: String? = null
    ) = getResult<ApiResponseWrapper<ApplyPromoResponse>> {
        client.post {
            url(Endpoints.APPLY_PROMO_CODE)
            setBody(deviceDetails)
            parameter("promoCode", promoCode)
            parameter("id", id)
            parameter("type", type)
        }
    }

    suspend fun fetchUserSavedVPAs() =
        getResult<ApiResponseWrapper<SavedVpaResponse>> {
            client.get {
                url(Endpoints.FETCH_USER_SAVED_VPA)
            }
        }

    suspend fun addNewVPA(vpaName: String) =
        getResult<ApiResponseWrapper<SavedVPA?>> {
            client.post {
                url(Endpoints.ADD_NEW_VPA)
                setBody(
                    JsonObject(
                        mapOf(
                            Pair("vpa", JsonPrimitive(vpaName))
                        )
                    )
                )
            }
        }

    suspend fun updateUserDeviceDetails(userDeviceDetails: UserDeviceDetails) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_USER_DEVICE_DETAIL)
                setBody(userDeviceDetails)
            }
        }

    suspend fun verifyPhoneNumber(otpLoginRequest: OTPLoginRequest) =
        getResult<ApiResponseWrapper<String>> {
            client.post {
                url(Endpoints.VERIFY_PHONE_NUMBER)
                setBody(otpLoginRequest)
            }
        }

    suspend fun updateFcmToken(fcmToken: String, instanceId: String?) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_FCM_TOKEN)
                setBody(
                    JsonObject(
                        mapOf(
                            Pair("token", JsonPrimitive(fcmToken)),
                            Pair("instanceId", JsonPrimitive(instanceId))
                        )
                    )
                )
            }
        }

    suspend fun submitUserRating(json: JsonObject) =
        getResult<ApiResponseWrapper<String>> {
            client.post {
                url(Endpoints.SUBMIT_USER_REVIEW)
                setBody(json)
            }
        }

    suspend fun getUserRating() =
        getResult<ApiResponseWrapper<UserRatingData?>> {
            client.get {
                url(Endpoints.GET_USER_RATING)
            }
        }

    suspend fun fetchVpaChips() =
        getResult<ApiResponseWrapper<VpaChips>> {
            client.get {
                url(Endpoints.FETCH_VPA_CHIPS)
            }
        }
}