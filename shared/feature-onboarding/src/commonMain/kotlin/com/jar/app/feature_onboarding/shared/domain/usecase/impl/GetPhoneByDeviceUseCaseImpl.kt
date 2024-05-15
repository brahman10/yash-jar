package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.IGetPhoneByDeviceUseCase

internal class GetPhoneByDeviceUseCaseImpl constructor(
    private val repository: LoginRepository
) : IGetPhoneByDeviceUseCase {
    override suspend fun getPhoneByDevice(getPhoneRequest: com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest) =
        repository.getPhoneByDevice(getPhoneRequest)
}