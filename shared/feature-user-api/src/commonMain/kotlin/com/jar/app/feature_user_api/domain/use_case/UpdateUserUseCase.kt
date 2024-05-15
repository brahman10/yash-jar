package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.core_base.domain.model.User
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserUseCase {

    suspend fun updateUser(user: User): Flow<RestClientResult<ApiResponseWrapper<User?>>>

}