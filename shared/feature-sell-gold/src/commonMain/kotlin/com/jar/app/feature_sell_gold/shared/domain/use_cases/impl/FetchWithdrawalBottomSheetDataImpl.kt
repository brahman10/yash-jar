package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase

internal class FetchWithdrawalBottomSheetDataImpl constructor(
    private val repository: IWithdrawalRepository
) : IFetchWithdrawalBottomSheetDataUseCase {
    override suspend fun fetchWithdrawBottomSheetData() =
        repository.fetchWithdrawBottomSheetData()
}