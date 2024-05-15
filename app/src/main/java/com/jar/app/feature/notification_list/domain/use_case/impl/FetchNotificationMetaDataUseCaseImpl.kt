package com.jar.app.feature.notification_list.domain.use_case.impl

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import com.jar.app.feature.notification_list.domain.use_case.FetchNotificationMetaDataUseCase
import com.jar.app.feature_user_api.domain.mappers.toUserMetaData
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchNotificationMetaDataUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : FetchNotificationMetaDataUseCase {

    override suspend fun fetchNotificationMetaData(): Flow<RestClientResult<NotificationMetaData>> =
        flow {
            val localState = homeRepository.fetchLocalUserMetaData()
            userRepository.fetchRemoteUserMetaData().collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val remoteState = it
                    if (homeRepository.fetchUserMetaDataRowCount() == 0L)
                        homeRepository.insertLocalUserMetaData(remoteState.toUserMetaData())
                    else if (localState?.notificationCount != remoteState.notificationCount) {
                        emit(
                            RestClientResult.success(
                                NotificationMetaData(remoteState.notificationCount)
                            )
                        )
                    }
                },
                onError = { errorMessage, errorCode ->
                    emit(RestClientResult.error(errorMessage))
                }
            )
        }

    override suspend fun updateNotificationMetaData(notificationMetaData: NotificationMetaData) {
        homeRepository.updateLocalNotificationMetaData(notificationMetaData)
    }
}