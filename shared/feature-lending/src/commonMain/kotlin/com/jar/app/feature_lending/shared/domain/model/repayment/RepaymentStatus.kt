package com.jar.app.feature_lending.shared.domain.model.repayment

enum class RepaymentStatus {
    PAYMENT_FAILED,
    PAYMENT_PENDING,
    LATE_PAYMENT,
    PAID_ON_TIME,
    PAYMENT_OVERDUE,
    UPCOMING;
    companion object {
        fun getPaymentStatus(typeString: String): RepaymentStatus {
            return when(typeString) {
                "PAYMENT_FAILED" -> RepaymentStatus.PAYMENT_FAILED
                "PAYMENT_PENDING" -> RepaymentStatus.PAYMENT_PENDING
                "LATE_PAYMENT"-> RepaymentStatus.LATE_PAYMENT
                "PAID_ON_TIME"-> RepaymentStatus.PAID_ON_TIME
                "PAYMENT_OVERDUE"-> RepaymentStatus.PAYMENT_OVERDUE
                "UPCOMING"-> RepaymentStatus.UPCOMING
                else -> RepaymentStatus.PAYMENT_FAILED
            }
        }
    }
}