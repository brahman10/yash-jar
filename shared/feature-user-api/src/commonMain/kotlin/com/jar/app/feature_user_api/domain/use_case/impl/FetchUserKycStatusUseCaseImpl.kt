package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase

internal class FetchUserKycStatusUseCaseImpl constructor(
    private val userRepository: UserRepository
): FetchUserKycStatusUseCase {

    override suspend fun fetchUserKycStatus(kycContext: String?) = userRepository.fetchUserKycStatus(kycContext)

}