package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.RequestLendingOtpUseCase

internal class RequestLendingOtpUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : RequestLendingOtpUseCase {

    override suspend fun requestLendingOtp(loanId: String,type:String) = lendingRepository.requestLendingOtp(loanId,type)

}