package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.RepaymentEntryRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.PostRepaymentEntryRequestUseCase

internal class PostRepaymentEntryRequestUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): PostRepaymentEntryRequestUseCase {

    override suspend fun postRepaymentEntryRequest(repaymentEntryRequest: RepaymentEntryRequest) =
        vasooliRepository.postRepaymentEntryRequest(repaymentEntryRequest)

}