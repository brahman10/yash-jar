package com.jar.app.feature_profile.di

import com.jar.app.feature_profile.data.network.UserDataSource
import com.jar.app.feature_profile.data.repository.UserRepository
import com.jar.app.feature_profile.domain.repository.UserRepositoryImpl
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.domain.use_case.RequestOtpUseCase
import com.jar.app.feature_profile.domain.use_case.impl.FetchDashboardStaticContentUseCaseImpl
import com.jar.app.feature_profile.domain.use_case.impl.RequestOtpUseCaseImpl
import io.ktor.client.HttpClient

class ProfileModule(client: HttpClient) {


    private val userDataSource: UserDataSource by lazy {
        UserDataSource(client)
    }

    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userDataSource)
    }

    val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase by lazy {
        FetchDashboardStaticContentUseCaseImpl(userRepository)
    }

    val requestOtpUseCase: RequestOtpUseCase by lazy {
        RequestOtpUseCaseImpl(userRepository)
    }

}