package com.jar.app.feature_user_api.data.network

import com.jar.app.core_base.data.dto.GoldBalanceDTO
import com.jar.app.core_base.domain.model.User
import com.jar.app.feature_user_api.data.dto.DetectedSpendsDTO
import com.jar.app.feature_user_api.data.dto.UserGoldSipDetailsDTO
import com.jar.app.feature_user_api.data.dto.UserMetaDTO
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.data.BaseRepositoryV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UserRepository : BaseRepositoryV2 {

    suspend fun fetchUserSettings(): Flow<RestClientResult<ApiResponseWrapper<UserSettingsDTO>>>

    suspend fun updateUserSettings(userSettings: UserSettingsDTO): Flow<RestClientResult<ApiResponseWrapper<UserSettingsDTO>>>

    suspend fun fetchDetectedSpendInfo(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<DetectedSpendsDTO>>>

    suspend fun verifyPhoneNumber(otpLoginRequest: OTPLoginRequest): Flow<RestClientResult<ApiResponseWrapper<String>>>

    suspend fun updateUserPhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData>>>

    suspend fun updateUser(user: User): Flow<RestClientResult<ApiResponseWrapper<User?>>>

    suspend fun updateUserProfilePhoto(byteArray: ByteArray): Flow<RestClientResult<ApiResponseWrapper<UserProfilePicture>>>

    suspend fun validatePinCode(pinCode: String): Flow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse>>>

    suspend fun fetchUserGoldBalance(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<GoldBalanceDTO?>>>

    suspend fun deleteAddress(id: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun getAllAddress(): Flow<RestClientResult<ApiResponseWrapper<Addresses>>>

    suspend fun addDeliveryAddress(address: Address): Flow<RestClientResult<ApiResponseWrapper<Address>>>

    suspend fun editAddress(
        id: String,
        address: Address
    ): Flow<RestClientResult<ApiResponseWrapper<Address>>>

    suspend fun getAddressById(id: String): Flow<RestClientResult<ApiResponseWrapper<Address>>>

    suspend fun fetchUserSavedVPAs(): Flow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>

    suspend fun addNewVPA(vpaName: String): Flow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>

    suspend fun deleteUserSavedVPA(savedVpaId: String): Flow<RestClientResult<ApiResponseWrapper<String?>>>

    suspend fun isAutoInvestResetRequired(
        newAmount: Float,
        savingsType: String
    ): Flow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>

    suspend fun fetchRemoteUserMetaData(): Flow<RestClientResult<ApiResponseWrapper<UserMetaDTO>>>

    suspend fun fetchUserKycStatus(kycContext: String?): Flow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>

    suspend fun fetchUserWinnings(): Flow<RestClientResult<ApiResponseWrapper<WinningResponse>>>

    suspend fun fetchGoldSipDetails(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetailsDTO>>>

    suspend fun updatePauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        pauseType: String
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
}