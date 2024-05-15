package com.jar.app.feature.home.data.repository

import com.jar.app.feature.home.domain.model.ApplyPromoResponse
import com.jar.app.feature.home.domain.model.UserDeviceDetails
import com.jar.app.feature.home.domain.model.UserRatingData
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdownResponse
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.model.SavedVpaResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface UserRepository : BaseRepository {

    suspend fun fetchUserSavedVPAs(): Flow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>

    suspend fun addNewVPA(vpaName: String): Flow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>

    suspend fun updateUserDeviceDetails(userDeviceDetails: UserDeviceDetails): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun applyPromoCode(
        deviceDetails: DeviceDetails,
        promoCode: String,
        id: String? = null,
        type: String? = null
    ): Flow<RestClientResult<ApiResponseWrapper<ApplyPromoResponse>>>

    suspend fun updateFcmToken(fcmToken: String, instanceId: String?): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun submitUserRating(json: JsonObject): Flow<RestClientResult<ApiResponseWrapper<String>>>

    suspend fun getUserRating(): Flow<RestClientResult<ApiResponseWrapper<UserRatingData?>>>
}