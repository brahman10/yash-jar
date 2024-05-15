package com.jar.app.feature_lending.shared.domain.ui_event


sealed class RefreshCreditScoreBottomSheetEvent {
    data class OnNameUpdate(val name: String,val isCheckCreditScore:Boolean = false) : RefreshCreditScoreBottomSheetEvent()
    data class OnPanUpdate(val panNumber: String,val isCheckCreditScore:Boolean = false) : RefreshCreditScoreBottomSheetEvent()

    object OnClickSubmitButtonInRefreshScoreBottomSheet : RefreshCreditScoreBottomSheetEvent()

}
