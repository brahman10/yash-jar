package com.jar.app.feature_lending.shared.domain.ui_event

import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.AddBankDetailsState

sealed class AddBankDetailsEvent{
    data class updateAccountNo(val accountNo : String):AddBankDetailsEvent()
    data class updateIfscCode(val ifscCode : String):AddBankDetailsEvent()
    data class onButtonClick(val uiState: AddBankDetailsState) : AddBankDetailsEvent()
}
