package com.jar.app.feature_lending.shared.util

import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus

object LendingUtil {
    fun isWithdrawalSuccess(withdrawalStatus: String?) =
        withdrawalStatus == LoanStatus.IN_PROGRESS.name ||
                withdrawalStatus == LoanStatus.VERIFIED.name
}