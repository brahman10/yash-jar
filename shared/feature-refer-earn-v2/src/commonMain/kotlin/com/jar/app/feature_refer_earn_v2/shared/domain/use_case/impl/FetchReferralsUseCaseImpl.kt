package com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl

import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2Repository
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase

internal class FetchReferralsUseCaseImpl constructor(
    private val referEarnV2Repository: ReferEarnV2Repository
) : FetchReferralsUseCase {

    override suspend fun fetchReferrals(
        page: Int,
        size: Int
    ) =
        referEarnV2Repository.fetchReferrals(page, size)

}