package com.jar.app.feature_user_api.domain.network

import com.jar.app.core_base.data.dto.GoldBalanceDTO
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.data.network.UserDataSource
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.model.PhoneNumberWithCountryCode
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import kotlinx.coroutines.flow.Flow

internal class UserRepositoryImpl constructor(
    private val userDataSource: UserDataSource,
    private val serializer: Serializer,
    private val prefsApi: PrefsApi
) : UserRepository {

    override suspend fun fetchUserSettings() = getFlowResult { userDataSource.fetchUserSettings() }

    override suspend fun updateUserSettings(userSettings: UserSettingsDTO) =
        getFlowResult { userDataSource.updateUserSettings(userSettings) }

    override suspend fun fetchDetectedSpendInfo(includeView: Boolean) = getFlowResult {
        userDataSource.fetchDetectedSpendInfo(includeView)
    }

    override suspend fun verifyPhoneNumber(otpLoginRequest: OTPLoginRequest) =
        getFlowResult { userDataSource.verifyPhoneNumber(otpLoginRequest) }

    override suspend fun updateUserPhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode) =
        getFlowResult { userDataSource.updateUserPhoneNumber(phoneNumberWithCountryCode) }

    override suspend fun updateUser(user: User) = getFlowResult { userDataSource.updateUser(user) }

    override suspend fun updateUserProfilePhoto(byteArray: ByteArray) =
        getFlowResult { userDataSource.updateUserProfilePhoto(byteArray) }

    override suspend fun validatePinCode(pinCode: String) =
        getFlowResult { userDataSource.validatePinCode(pinCode) }

    override suspend fun fetchUserGoldBalance(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<GoldBalanceDTO?>>> =
        getFlowResultV2(
            fetchResultFromServer = userDataSource.fetchUserGoldBalance(includeView),
            fetchResultFromCache = {
                prefsApi.getStringData(it)
            },
            storeResultInCache = { key, value ->
                prefsApi.setStringData(key, serializer.encodeToString(value))
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun getAllAddress() = getFlowResult {
        userDataSource.getAllAddress()
    }

    override suspend fun deleteAddress(id: String) =
        getFlowResult { userDataSource.deleteAddress(id) }

    override suspend fun addDeliveryAddress(address: Address) =
        getFlowResult { userDataSource.addDeliveryAddress(address) }

    override suspend fun editAddress(
        id: String,
        address: Address
    ) = getFlowResult { userDataSource.editAddress(id, address) }

    override suspend fun getAddressById(id: String) =
        getFlowResult { userDataSource.getAddressById(id) }

    override suspend fun fetchUserSavedVPAs() =
        getFlowResult { userDataSource.fetchUserSavedVPAs() }

    override suspend fun addNewVPA(vpaName: String) =
        getFlowResult { userDataSource.addNewVPA(vpaName) }

    override suspend fun deleteUserSavedVPA(savedVpaId: String) =
        getFlowResult { userDataSource.deleteVPA(savedVpaId) }

    override suspend fun isAutoInvestResetRequired(newAmount: Float, savingsType: String) =
        getFlowResult { userDataSource.isAutoInvestResetRequired(newAmount, savingsType) }

    override suspend fun fetchRemoteUserMetaData() =
        getFlowResult { userDataSource.fetchRemoteUserMetaData() }

    override suspend fun fetchUserKycStatus(kycContext: String?) =
        getFlowResult { userDataSource.fetchUserKycStatus(kycContext) }

    override suspend fun fetchUserWinnings() = getFlowResult { userDataSource.fetchUserWinnings() }

    override suspend fun fetchGoldSipDetails(includeView: Boolean) = getFlowResult {
        userDataSource.fetchGoldSipDetails(includeView)
    }

    override suspend fun updatePauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        pauseType: String
    ) =
        getFlowResult {
            userDataSource.updatePauseDuration(pause, pauseDuration, pauseType)
        }
}