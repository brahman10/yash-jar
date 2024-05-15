package com.jar.app.base.data.event

data class UpdateDSAutopayBankEvent(
    val newDSAmount: Float,
    val mandateAmount: Float,
    val isRoundOffEnabled: Boolean
)