package com.jar.app.feature.home.data.repository

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature.home.domain.model.AdSourceData
import com.jar.app.feature.home.domain.model.DashboardStaticData
import com.jar.app.feature.home.domain.model.DowntimeResponse
import com.jar.app.feature.home.domain.model.ForceUpdateResponse
import com.jar.app.feature.home.domain.model.IsKycRequiredData
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import com.jar.app.feature.promo_code.domain.data.PromoCode
import com.jar.app.feature.survey.domain.model.SubmitSurveyResponse
import com.jar.app.feature.survey.domain.model.Survey
import com.jar.app.feature_user_api.domain.model.UserMetaData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement

interface HomeRepository : BaseRepository {

    suspend fun fetchDownTime(): Flow<RestClientResult<ApiResponseWrapper<DowntimeResponse?>>>

    suspend fun updateSession(appVersion: Int): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>

    suspend fun fetchPublicStaticContent(
        staticContentType: StaticContentType,
        phoneNumber: String,
        context: String?
    ): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>

    suspend fun fetchUserSurvey(): RestClientResult<ApiResponseWrapper<Survey?>>

    suspend fun submitSurvey(jsonElement: JsonElement): RestClientResult<ApiResponseWrapper<SubmitSurveyResponse>>

    suspend fun fetchPromoCode(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<PromoCode>>>

    suspend fun insertLocalUserMetaData(userMetaData: UserMetaData)

    suspend fun fetchUserMetaDataRowCount(): Long

    suspend fun updateLocalNotificationMetaData(notificationMetaData: NotificationMetaData)

    suspend fun fetchLocalUserMetaData(): UserMetaData?

    suspend fun fetchActiveAnalyticsList(): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData>>>

    suspend fun updateAdSourceData(adSourceData: AdSourceData): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun captureAppOpens(): Flow<RestClientResult<ApiResponseWrapper<Boolean>>>

    suspend fun fetchForceUpdateData(): Flow<RestClientResult<ApiResponseWrapper<ForceUpdateResponse>>>

    suspend fun fetchIfKycIsRequired(): Flow<RestClientResult<ApiResponseWrapper<IsKycRequiredData>>>
}