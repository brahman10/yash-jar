package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.OtpVerifyRequestData
import com.jar.app.feature_lending.shared.domain.use_case.VerifyLendingOtpUseCase

internal class VerifyLendingOtpUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : VerifyLendingOtpUseCase {

    override suspend fun verifyLendingOtp(data: OtpVerifyRequestData) =
        lendingRepository.verifyLendingOtp(data)

}