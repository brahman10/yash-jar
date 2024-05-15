package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.LogoutUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LogoutUseCaseImpl constructor(
    private val loginRepository: LoginRepository,
    private val clearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase,
    private val appHttpClient: HttpClient,
    private val prefsApi: PrefsApi
) : LogoutUseCase {

    override suspend fun logout(
        deviceId: String?,
        refreshToken: String?
    ): Flow<RestClientResult<ApiResponseWrapper<String?>>> {

        clearCachedHomeFeedUseCase.clearAllHomeFeedData() // Clear Home Feed in DB

        prefsApi.clearAll() // Clear All Preferences

        appHttpClient
            .plugin(Auth)
            .providers
            .filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken() // Clear Token From KTOR client

        return if (prefsApi.isLoggedIn().not()) {
            flow { RestClientResult.none<ApiResponseWrapper<String?>>() }
        } else {
            loginRepository.logout(deviceId, refreshToken)
        }
    }

}