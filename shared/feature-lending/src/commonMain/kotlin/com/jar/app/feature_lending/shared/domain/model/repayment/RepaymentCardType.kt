package com.jar.app.feature_lending.shared.domain.model.repayment

import com.jar.app.core_base.domain.model.WinningsType

enum class RepaymentCardType {
    UPCOMING,
    PAID,
    FAILED,
    SCHEDULED,
    PENDING;
    companion object {
        fun getRepaymentCardType(typeString: String): RepaymentCardType {
            return when(typeString) {
                "UPCOMING" -> RepaymentCardType.UPCOMING
                "PAID" -> RepaymentCardType.PAID
                "FAILED"-> RepaymentCardType.FAILED
                "SCHEDULED"-> RepaymentCardType.SCHEDULED
                "PENDING"-> RepaymentCardType.PENDING
                else -> RepaymentCardType.FAILED
            }
        }
    }
}