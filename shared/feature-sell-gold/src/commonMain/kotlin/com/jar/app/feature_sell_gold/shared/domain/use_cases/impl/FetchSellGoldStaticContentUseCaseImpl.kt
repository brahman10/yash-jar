package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchSellGoldStaticContentUseCase

internal class FetchSellGoldStaticContentUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IFetchSellGoldStaticContentUseCase {
    override suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        repository.fetchDashboardStaticContent(staticContentType)
}