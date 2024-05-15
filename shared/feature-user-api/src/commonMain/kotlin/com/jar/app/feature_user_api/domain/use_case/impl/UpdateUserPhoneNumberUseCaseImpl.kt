package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.PhoneNumberWithCountryCode
import com.jar.app.feature_user_api.domain.use_case.UpdateUserPhoneNumberUseCase

internal class UpdateUserPhoneNumberUseCaseImpl constructor(private val userRepository: UserRepository) :
    UpdateUserPhoneNumberUseCase {

    override suspend fun updateUserPhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode) =
        userRepository.updateUserPhoneNumber(phoneNumberWithCountryCode)

}