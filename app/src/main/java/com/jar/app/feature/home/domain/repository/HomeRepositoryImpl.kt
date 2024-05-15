package com.jar.app.feature.home.domain.repository

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.model.AdSourceData
import com.jar.app.feature.home.data.network.HomeDataSource
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import com.jar.app.feature_user_api.data.db.UserMetaLocalDataSource
import com.jar.app.feature_user_api.domain.model.UserMetaData

import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

internal class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource,
    private val userMetaLocalDataSource: UserMetaLocalDataSource
) : HomeRepository {

    override suspend fun fetchDownTime() = getFlowResult { homeDataSource.fetchDowntime() }

    override suspend fun updateSession(appVersion: Int) =
        getFlowResult { homeDataSource.updateSession(appVersion) }

    override suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        getFlowResult { homeDataSource.fetchDashboardStaticContent(staticContentType) }

    override suspend fun fetchPublicStaticContent(
        staticContentType: StaticContentType,
        phoneNumber: String,
        context: String?
    ) =
        getFlowResult {
            homeDataSource.fetchPublicStaticContent(
                staticContentType,
                phoneNumber,
                context
            )
        }


    override suspend fun fetchUserSurvey() = homeDataSource.getSurvey()

    override suspend fun submitSurvey(jsonElement: JsonElement) =
        homeDataSource.submitSurvey(jsonElement)

    override suspend fun fetchPromoCode(
        page: Int,
        size: Int
    ) = homeDataSource.fetchPromoCode(page, size)

    override suspend fun insertLocalUserMetaData(userMetaData: UserMetaData) =
        userMetaLocalDataSource.deleteAndInsertUserMetaData(userMetaData)

    override suspend fun fetchUserMetaDataRowCount() =
        userMetaLocalDataSource.fetchUserMetaDataRowCount()

    override suspend fun updateLocalNotificationMetaData(notificationMetaData: NotificationMetaData) =
        userMetaLocalDataSource.updateNotificationMetaData(notificationMetaData.notificationCount)

    override suspend fun fetchLocalUserMetaData() = userMetaLocalDataSource.fetchUserMetaData()

    override suspend fun fetchActiveAnalyticsList() =
        getFlowResult { homeDataSource.fetchActiveAnalyticsList() }

    override suspend fun updateAdSourceData(adSourceData: AdSourceData) =
        getFlowResult { homeDataSource.updateAdSourceData(adSourceData) }

    override suspend fun captureAppOpens() = getFlowResult {
        homeDataSource.captureAppOpens()
    }

    override suspend fun fetchForceUpdateData() = getFlowResult {
        homeDataSource.fetchForceUpdateData()
    }

    override suspend fun fetchIfKycIsRequired() = getFlowResult {
        homeDataSource.fetchIfKycIsRequired()
    }

}