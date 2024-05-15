package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.domain.model.UserProfilePicture
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserProfilePicUseCase {

    suspend fun updateUserProfilePhoto(byteArray: ByteArray): Flow<RestClientResult<ApiResponseWrapper<UserProfilePicture>>>
}