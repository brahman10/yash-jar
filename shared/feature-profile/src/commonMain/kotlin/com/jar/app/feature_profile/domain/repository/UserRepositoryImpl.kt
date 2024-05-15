package com.jar.app.feature_profile.domain.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_profile.data.network.UserDataSource
import com.jar.app.feature_profile.data.repository.UserRepository

internal class UserRepositoryImpl constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun requestOTP(
        phoneNumber: String,
        countryCode: String
    ) = getFlowResult { userDataSource.requestOTP(phoneNumber, countryCode) }

    override suspend fun requestOTPViaCall(
        phoneNumber: String,
        countryCode: String
    ) = getFlowResult { userDataSource.requestOTPViaCall(phoneNumber, countryCode) }

    override suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType) =
        getFlowResult { userDataSource.fetchDashboardStaticContent(staticContentType) }

}