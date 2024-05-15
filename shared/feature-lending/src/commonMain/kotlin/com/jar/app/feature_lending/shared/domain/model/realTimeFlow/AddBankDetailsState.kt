package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

data class AddBankDetailsState(
    val bankAccountNumber : String = "",
    val ifscCode : String = "",
    val bankImageUrl : String = "",
    val bankAddress : String = "",
    val bankName : String = "",
    val uspText : String = "",
    val errorInIfscCode : Boolean = false,
    val ifscCodeErrorMessage : String = "",
    val realTimeBankDetailSteps: List<RealTimeUiStep>? = null,
    ){
    fun shouldEnableButton() = bankAccountNumber.isNotEmpty() && ifscCode.length == 11
}
