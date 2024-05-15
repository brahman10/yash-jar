package com.jar.app.feature.home.domain.repository

import com.jar.app.feature.home.data.network.UserDataSource
import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.model.UserDeviceDetails
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature_user_api.domain.model.SavedVPA
import kotlinx.serialization.json.JsonObject

internal class UserRepositoryImpl constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override suspend fun fetchUserSavedVPAs() =
        getFlowResult { userDataSource.fetchUserSavedVPAs() }

    override suspend fun addNewVPA(vpaName: String) =
        getFlowResult { userDataSource.addNewVPA(vpaName) }

    override suspend fun updateUserDeviceDetails(userDeviceDetails: UserDeviceDetails) =
        getFlowResult { userDataSource.updateUserDeviceDetails(userDeviceDetails) }

    override suspend fun applyPromoCode(
        deviceDetails: DeviceDetails,
        promoCode: String,
        id: String?,
        type: String?
    ) = getFlowResult {
        userDataSource.applyPromoCode(deviceDetails, promoCode, id, type)
    }

    override suspend fun updateFcmToken(fcmToken: String, instanceId: String?) = getFlowResult {
        userDataSource.updateFcmToken(fcmToken, instanceId)
    }

    override suspend fun submitUserRating(json: JsonObject) = getFlowResult {
        userDataSource.submitUserRating(json)
    }

    override suspend fun getUserRating() = getFlowResult {
        userDataSource.getUserRating()
    }
}