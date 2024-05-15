package com.jar.app.feature.home.domain.usecase.impl

import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.model.PopupMetaData
import com.jar.app.feature.home.domain.usecase.FetchPopupMetaDataUseCase
import com.jar.app.feature_user_api.domain.mappers.toUserMetaData
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchPopupMetaDataUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : FetchPopupMetaDataUseCase {
    override suspend fun fetchPopupMetaData(): Flow<RestClientResult<PopupMetaData>> =
        flow {
            val localState = homeRepository.fetchLocalUserMetaData()
            userRepository.fetchRemoteUserMetaData().collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val remoteState = it
                    if (localState?.popupType != remoteState.popupType) {
                        emit(
                            RestClientResult.success(PopupMetaData(remoteState.popupType))
                        )
                    }
                    homeRepository.insertLocalUserMetaData(remoteState.toUserMetaData())
                },
                onError = { errorMessage, errorCode ->
                    emit(RestClientResult.error(errorMessage))
                }
            )
        }
}