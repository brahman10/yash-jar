package com.jar.app.feature_user_api.data.network

import com.jar.app.core_base.data.dto.GoldBalanceDTO
import com.jar.app.core_base.domain.model.User
import com.jar.app.feature_user_api.data.dto.DetectedSpendsDTO
import com.jar.app.feature_user_api.data.dto.UserGoldSipDetailsDTO
import com.jar.app.feature_user_api.data.dto.UserMetaDTO
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.model.*
import com.jar.app.feature_user_api.util.UserApiConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class UserDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchUserSettings() = getResult<ApiResponseWrapper<UserSettingsDTO>> {
        client.get {
            url(Endpoints.FETCH_USER_SETTINGS)
        }
    }

    suspend fun updateUserSettings(userSettings: UserSettingsDTO) =
        getResult<ApiResponseWrapper<UserSettingsDTO>> {
            client.post {
                url(Endpoints.UPDATE_USER_SETTINGS)
                setBody(userSettings)
            }
        }

    suspend fun fetchDetectedSpendInfo(includeView: Boolean) =
        getResult<ApiResponseWrapper<DetectedSpendsDTO>> {
            client.get {
                url(Endpoints.FETCH_DETECTED_SPEND_INFO)
                parameter("includeView", includeView)
            }
        }

    suspend fun updateUser(user: User) = getResult<ApiResponseWrapper<User?>> {
        client.post {
            url(Endpoints.UPDATE_USER)
            setBody(user)
        }
    }

    suspend fun updateUserProfilePhoto(byteArray: ByteArray) =
        getResult<ApiResponseWrapper<UserProfilePicture>> {
            client.put {
                url(Endpoints.UPDATE_USER_PROFILE_PICTURE)
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            "part",
                            byteArray,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "images/*"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=profile_picture"
                                )
                            }
                        )
                    }
                ))
            }
        }

    suspend fun updateUserPhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode) =
        getResult<ApiResponseWrapper<RequestOtpData>> {
            client.put {
                url(Endpoints.UPDATE_USER_PHONE_NUMBER)
                setBody(phoneNumberWithCountryCode)
            }
        }

    suspend fun verifyPhoneNumber(otpLoginRequest: OTPLoginRequest) =
        getResult<ApiResponseWrapper<String>> {
            client.post {
                url(Endpoints.VERIFY_USER_PHONE_NUMBER)
                setBody(otpLoginRequest)
            }
        }

    suspend fun validatePinCode(pinCode: String) =
        getResult<ApiResponseWrapper<ValidatePinCodeResponse>> {
            client.get {
                url(Endpoints.VALIDATE_PIN_CODE)
                parameter("pincode", pinCode)
            }
        }

    suspend fun fetchUserGoldBalance(includeView: Boolean) =
        getResultV2<ApiResponseWrapper<GoldBalanceDTO?>>(
            getCachingKey = {
                "user_gold_balance"
            },
            apiCall = {
                client.get {
                    url(Endpoints.FETCH_USER_GOLD_BALANCE)
                    parameter("includeView", includeView)
                }
            }
        )

    suspend fun deleteAddress(id: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.delete {
                url(Endpoints.DELETE_USER_ADDRESS)
                parameter("id", id)
            }
        }

    suspend fun getAllAddress() =
        getResult<ApiResponseWrapper<Addresses>> {
            client.get {
                url(Endpoints.FETCH_USER_ADDRESSES)
            }
        }

    suspend fun addDeliveryAddress(address: Address) =
        getResult<ApiResponseWrapper<Address>> {
            client.post {
                url(Endpoints.ADD_USER_ADDRESS)
                setBody(address)
            }
        }

    suspend fun editAddress(id: String, address: Address) =
        getResult<ApiResponseWrapper<Address>> {
            client.put {
                url(Endpoints.EDIT_USER_ADDRESS)
                parameter("id", id)
                setBody(address)
            }
        }

    suspend fun getAddressById(id: String) =
        getResult<ApiResponseWrapper<Address>> {
            client.get {
                url(Endpoints.FETCH_ADDRESS_BY_ID)
                parameter("id", id)
            }
        }

    suspend fun fetchUserSavedVPAs() =
        getResult<ApiResponseWrapper<SavedVpaResponse>> {
            client.get {
                url(Endpoints.FETCH_USER_SAVED_VPA)
            }
        }

    suspend fun deleteVPA(savedVpaId: String) =
        getResult<ApiResponseWrapper<String?>> {
            client.delete {
                url(Endpoints.DELETE_VPA)
                parameter("savedVpaId", savedVpaId)
            }
        }

    suspend fun addNewVPA(vpaName: String) =
        getResult<ApiResponseWrapper<SavedVPA?>> {
            client.post {
                url(Endpoints.ADD_NEW_VPA)
                setBody(JsonObject(mapOf(Pair("vpa", JsonPrimitive(vpaName)))))
            }
        }

    suspend fun isAutoInvestResetRequired(newAmount: Float, savingsType: String) =
        getResult<ApiResponseWrapper<AutopayResetRequiredResponse>> {
            client.get {
                url(Endpoints.IS_MANDATE_RESET_REQUIRED)
                parameter("amount", newAmount)
                parameter("savingsType", savingsType)
            }
        }

    suspend fun fetchRemoteUserMetaData() =
        getResult<ApiResponseWrapper<UserMetaDTO>> {
            client.get {
                url(Endpoints.FETCH_USER_UPDATES)
            }
        }

    suspend fun fetchUserKycStatus(kycContext: String?) =
        getResult<ApiResponseWrapper<UserKycStatus?>> {
            client.get {
                url(Endpoints.FETCH_KYC_STATUS)
                parameter("kycContext", kycContext)
            }
        }

    suspend fun fetchUserWinnings() =
        getResult<ApiResponseWrapper<WinningResponse>> {
            client.get {
                url(Endpoints.FETCH_USER_WINNINGS)
            }
        }

    suspend fun fetchGoldSipDetails(includeView: Boolean) =
        getResult<ApiResponseWrapper<UserGoldSipDetailsDTO>> {
            client.get {
                url(Endpoints.FETCH_GOLD_SIP_DETAILS)
                parameter("includeView", includeView)
            }
        }

    suspend fun updatePauseDuration(pause: Boolean, pauseDuration: String?, pauseType: String) =
        getResult<ApiResponseWrapper<PauseSavingResponse>> {
            client.get {
                url(Endpoints.UPDATE_PAUSE_DURATION)
                parameter("pause", pause)
                parameter("pauseDuration", pauseDuration)
                parameter("pauseType", pauseType)
            }
        }
}